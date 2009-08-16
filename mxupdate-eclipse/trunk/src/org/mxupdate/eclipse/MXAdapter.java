/*
 * Copyright 2008-2009 The MxUpdate Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.mxupdate.eclipse;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mxupdate.eclipse.console.Console;

/**
 * Adapter to the MX database.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MXAdapter
        implements IDeploymentAdapter
{
    /**
     * Key of the URL preference.
     */
    public static final String PREF_URL = "url"; //$NON-NLS-1$

    /**
     * Key of the name preference.
     */
    public static final String PREF_NAME = "name"; //$NON-NLS-1$

    /**
     * Key of the password preference.
     */
    public static final String PREF_PASSWORD = "password"; //$NON-NLS-1$

    /**
     * Key of the update by file content preference.
     */
    public static final String PREF_UPDATE_FILE_CONTENT = "updateByFileContent"; //$NON-NLS-1$

    /**
     * Key name of the properties stored in the preferences.
     */
    private static final String PREF_PROPERTIES = "pluginProperties"; //$NON-NLS-1$

    /**
     * Name and place of the manifest file.
     *
     * @see #getPlugInVersion()
     */
    private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF"; //$NON-NLS-1$

    /**
     * Label of the bundle version within the <code>META-INF/MANIFEST.MF</code>
     * file.
     *
     * @see #getPlugInVersion()
     */
    private static final String TEXT_BUNDLE_VERSION = "Bundle-Version:"; //$NON-NLS-1$

    /**
     * Length of the label of the bundle version within the
     * <code>META-INF/MANIFEST.MF</code> file.
     *
     * @see #getPlugInVersion()
     */
    private static final int LENGTH_BUNDLE_VERSION = MXAdapter.TEXT_BUNDLE_VERSION.length();

    /**
     * Holds the link to the preferences.
     */
    private final IPreferenceStore preferences;

    /**
     * MxUpdate plug-in console.
     *
     * @see #showConsole()
     */
    private final Console console;

    /**
     * Context to the MX database.
     *
     * @see #connected
     * @see #connect()
     * @see #disconnect()
     */
    private Context mxContext;

    /**
     * Is {@link #mxContext} connected to the MX database?
     *
     * @see #mxContext
     * @see #connect()
     * @see #disconnect()
     */
    private boolean connected = false;

    /**
     * Initializes the MX adapter.
     *
     * @param _preferences  preference store
     * @param _console      console used for logging purposes
     */
    MXAdapter(final IPreferenceStore _preferences,
              final Console _console)
    {
        this.preferences = _preferences;
        this.console = _console;
    }

    /**
     * Connects to the MX database.
     *
     * @return <i>true</i> if already connected or connect to MX database was
     *         successfully; otherwise <i>false</i> is returned
     * @see #connected
     * @see #mxContext
     * @see #checkVersions()
     */
    public boolean connect()
    {
        boolean connect = false;
        if (this.connected)  {
            connect = true;
            this.console.logInfo(Messages.getString("MXAdapter.AlreadyConnected")); //$NON-NLS-1$
        } else  {
            final String host =  this.preferences.getString(MXAdapter.PREF_URL);
            final String user =  this.preferences.getString(MXAdapter.PREF_NAME);
            final String passwd =  this.preferences.getString(MXAdapter.PREF_PASSWORD);

            try {
                this.mxContext = new Context(host);
                this.mxContext.resetContext(user, passwd, null);
                this.mxContext.connect();
                this.connected = this.mxContext.isConnected();
                connect = true;
                this.console.logInfo(Messages.getString("MXAdapter.ConnectedTo", host)); //$NON-NLS-1$

                // read properties
                final String newProps = this.execute("exec prog org.mxupdate.plugin.GetProperties"); //$NON-NLS-1$
                final String curProps = this.preferences.getString(MXAdapter.PREF_PROPERTIES);
                if (!newProps.equals(curProps))  {
                    this.preferences.setValue(MXAdapter.PREF_PROPERTIES, newProps);
                    this.console.logInfo(Messages.getString("MXAdapter.PluginPropertiesChanged")); //$NON-NLS-1$
                }
            } catch (final MatrixException e) {
                this.console.logError(Messages.getString("MXAdapter.ConnectFailed"), e); //$NON-NLS-1$
            }

            // check versions
            if (this.connected)  {
                this.checkVersions();
            }
        }
        return connect;
    }

    /**
     * Checks that the version of the MxUpdate Eclipse Plug-In and the version
     * of the MxUpdate Update Deployment Tool have the same major and minor
     * number within their versions. If not equal the plug-in automatically
     * disconnects from MX.
     *
     * @see #disconnect()
     * @see #getPlugInVersion()
     * @see #connect()
     */
    protected void checkVersions()
    {
        String pluginVersion = null;
        try {
            pluginVersion = this.getPlugInVersion();
        } catch (final IOException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionGetPlugInVersion"), e); //$NON-NLS-1$
        }

        String updateVersion = null;
        try {
            updateVersion = this.execute("exec prog org.mxupdate.plugin.GetVersion").replaceAll("-", ".");
        } catch (final MatrixException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionGetUpdateVersion"), e); //$NON-NLS-1$
        }

        final String[] pluginVersions = (pluginVersion != null) ? pluginVersion.split("\\.") : null;
        final String[] updateVersions = (updateVersion != null) ? updateVersion.split("\\.") : null;
        if ((pluginVersion == null) || (pluginVersions.length < 2)
                || (updateVersion == null) || (updateVersions.length < 2)
                || !pluginVersions[0].equals(updateVersions[0]) || !pluginVersions[1].equals(updateVersions[1]))  {
            this.console.logError(Messages.getString("MXAdapter.CheckVersionsNoConnectAllowed", //$NON-NLS-1$
                                                     pluginVersion,
                                                     updateVersion));
        }
    }

    /**
     * Returns for this plug-in the version stored within manifest file.
     *
     * @return found plug-in version within manifest file
     * @throws IOException if the manifest file could not found or not opened
     * @see #MANIFEST_FILE
     */
    protected String getPlugInVersion()
            throws IOException
    {
        final InputStream in = this.getClass().getClassLoader().getResourceAsStream(MXAdapter.MANIFEST_FILE);
        final byte[] buffer = new byte[in.available()];
        in.read(buffer);
        in.close();

        final String manifest = new String(buffer);
        String version = null;
        for (final String line : manifest.split("\n"))  {
            if (line.startsWith(MXAdapter.TEXT_BUNDLE_VERSION))  {
                version = line.substring(MXAdapter.LENGTH_BUNDLE_VERSION).trim();
                break;
            }
        }

        return version;
    }

    /**
     * Disconnect from the MX database.
     *
     * @return <i>true</i> if already disconnected or disconnect from MX
     *         database was successfully; otherwise <i>false</i> is returned
     * @see #connected
     * @see #mxContext
     */
    public boolean disconnect()
    {
        boolean disconnect = false;
        if (!this.connected)  {
            this.console.logInfo(Messages.getString("MXAdapter.AlreadyDisconnected")); //$NON-NLS-1$
            disconnect = true;
        } else  {
            try {
                this.mxContext.disconnect();
                this.mxContext = null;
                this.connected = false;
                disconnect = true;
                this.console.logInfo(Messages.getString("MXAdapter.Disconnected")); //$NON-NLS-1$
            } catch (final MatrixException e) {
                this.console.logError(Messages.getString("MXAdapter.DisconnectFailed"), e); //$NON-NLS-1$
            }
        }
        return disconnect;
    }

    /**
     * Updates given MX update files in the MX database. If
     * {@link #PREF_UPDATE_FILE_CONTENT} is set, also the file content is
     * transfered within the update (e.g. if an update on another server is
     * done).
     *
     * @param _files    MxUpdate file which must be updated
     * @param _compile  if <i>true</i> all JPOs are compiled; if <i>false</i>
     *                  no JPOs are compiled, only an update is done
     * @see #execMql(CharSequence)
     */
    public void update(final List<IFile> _files,
                       final boolean _compile)
    {
        // update by file content
        if (this.preferences.getBoolean(MXAdapter.PREF_UPDATE_FILE_CONTENT))  {
            final Map<String,String> files = new HashMap<String,String>();
            for (final IFile file: _files)  {
                try  {
                    final InputStream in = new FileInputStream(file.getLocation().toFile());
                    final byte[] bytes = new byte[in.available()];
                    in.read(bytes);
                    in.close();
                    files.put(file.getLocation().toString(),
                              new String(bytes, file.getCharset()));
                } catch (final UnsupportedEncodingException e)  {
                    this.console.logError(Messages.getString("MXAdapter.ExceptionConvertFileContent", //$NON-NLS-1$
                                                             file.getLocation().toString()),
                                          e);
                } catch (final CoreException e) {
                    this.console.logError(Messages.getString("MXAdapter.ExceptionFileCharSet", //$NON-NLS-1$
                                                             file.getLocation().toString()),
                                          e);
                } catch (final IOException e) {
                    this.console.logError(Messages.getString("MXAdapter.ExceptionReadFileContentFailed", //$NON-NLS-1$
                                                             file.getLocation().toString()),
                                          e);
                }
            }
            try {
                this.console.logInfo(this.jpoInvoke("org.mxupdate.plugin.Update",
                                                    "updateByContent",
                                                    files,
                                                    _compile));
            } catch (final Exception e)  {
                this.console.logError(Messages.getString("MXAdapter.ExceptionUpdateFailed",  //$NON-NLS-1$
                                                         files.keySet().toString()),
                                      e);
            }
        // update by file names
        } else  {
            final Set<String> fileNames = new HashSet<String>();
            for (final IFile file: _files)  {
                fileNames.add(file.getLocation().toString());
            }
            try {
                this.console.logInfo(this.jpoInvoke("org.mxupdate.plugin.Update",
                                                    "updateByName",
                                                    fileNames,
                                                    _compile));
            } catch (final Exception e)  {
                this.console.logError(Messages.getString("MXAdapter.ExceptionUpdateFailed", //$NON-NLS-1$
                                                         fileNames.toString()),
                                      e);
            }
        }
    }

    /**
     * Extract the TCL update code for given <code>_file</code> from MX.
     *
     * @param _file     name of the update file for which the TCL update code
     *                  within MX must be extracted
     * @return configuration item update code for given <code>_file</code>
     * @throws MatrixException if update code could not be extracted
     */
    public String extractCode(final IFile _file)
            throws MatrixException
    {
        return this.execute("exec prog org.mxupdate.plugin.GetMxUpdateCode '" //$NON-NLS-1$
                + _file.toString() + "'"); //$NON-NLS-1$

    }

    /**
     * Executes given MQL command and returns the trimmed result of the MQL
     * execution. The MX context {@link #mxContext} is connected to the data
     * base if not already done.
     *
     * @param _command  MQL command to execute
     * @return trimmed result of the MQL execution
     * @throws MatrixException if MQL execution fails
     * @see #mxContext
     * @see #connect()
     */
    public String execute(final CharSequence _command)
            throws MatrixException
    {
        if (!this.connected)  {
            this.connect();
        }

        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(this.mxContext, _command.toString());
        if ((mql.getError() != null) && !"".equals(mql.getError()))  { //$NON-NLS-1$
            throw new MatrixException(mql.getError() + "\nMQL command was:\n" + _command);
        }
        return mql.getResult().trim();
    }

    /**
     * Calls given <code>_method</code> in <code>_jpo</code>. The MX context
     * {@link #mxContext} is connected to the database if not already done.
     *
     * @param _jpo          name of JPO to call
     * @param _method       method of the called <code>_jpo</code>
     * @param _parameters   list of all parameters for the <code>_jpo</code>
     *                      which are automatically encoded encoded
     * @return returned value from the called <code>_jpo</code>
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if the called <code>_jpo</code> throws an
     *                          exception
     * @see #mxContext
     * @see #connect()
     */
    protected String jpoInvoke(final String _jpo,
                               final String _method,
                               final Object... _parameters)
        throws IOException, MatrixException
    {
        if (!this.connected)  {
            this.connect();
        }

        // encode parameters
        final String[] paramStrings = new String[_parameters.length];
        int idx = 0;
        for (final Object parameter : _parameters)  {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(parameter);
            oos.close();
            paramStrings[idx++] = new String(Base64.encodeBase64(out.toByteArray()));
        }

        return (String) JPO.invoke(this.mxContext, _jpo, null, _method, paramStrings, String.class);
    }
}
