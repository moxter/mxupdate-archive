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

import java.util.List;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Preferences;
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
    public final static String PREF_URL = "url";

    /**
     * Key of the name preference.
     */
    public final static String PREF_NAME = "name";

    /**
     * Key of the password preference.
     */
    public final static String PREF_PASSWORD = "password";

    /**
     * Key of the update by file content preference.
     */
    public final static String PREF_UPDATE_FILE_CONTENT = "updateByFileContent";

    /**
     * Holds the link to the preferences.
     */
    private final Preferences preferences;

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

    MXAdapter(final Preferences _preferences,
                 final Console _console)
    {
        this.preferences = _preferences;
        this.console = _console;
    }

    /**
     *
     * @return <i>true</i> if already connected or connect to MX database was
     *         successfully; otherwise <i>false</i> is returned
     * @see #connected
     * @see #mxContext
     */
    public boolean connect()
    {
        boolean connect = false;
        if (this.connected)  {
            connect = true;
            this.console.logInfo(Messages.getString("MXAdapter.AlreadyConnected")); //$NON-NLS-1$
        } else  {
            final String host =  this.preferences.getString(PREF_URL);
            final String user =  this.preferences.getString(PREF_NAME);
            final String passwd =  this.preferences.getString(PREF_PASSWORD);

            try {
                this.mxContext = new Context(host);
                this.mxContext.resetContext(user, passwd, null);
                this.mxContext.connect();
                this.connected = this.mxContext.isConnected();
                connect = true;
                this.console.logInfo(Messages.getString("MXAdapter.ConnectedTo", host)); //$NON-NLS-1$

                // read properties
                final String newProps = this.execute("exec prog org.mxupdate.plugin.GetProperties"); //$NON-NLS-1$
                final String curProps = this.preferences.getString("pluginProperties"); //$NON-NLS-1$
                if (!newProps.equals(curProps))  {
                    this.preferences.setValue("pluginProperties", newProps); //$NON-NLS-1$
                    this.console.logInfo(Messages.getString("MXAdapter.PluginPropertiesChanged")); //$NON-NLS-1$
                }

            } catch (final MatrixException e) {
                this.console.logError(Messages.getString("MXAdapter.ConnectFailed"), e); //$NON-NLS-1$
            }
        }
        return connect;
    }

    /**
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
     *
     * @param _files    MxUpdate file which must be updated
     * @see #execMql(CharSequence)
     */
    public void update(final List<IFile> _files)
    {
        for (final IFile file: _files)  {
            final String fileStr = file.getLocation().toString();
            try {
                final String ret = this.execute(
                        "exec prog org.mxupdate.plugin.Update '" + fileStr + "';" //$NON-NLS-1$ //$NON-NLS-2$
                );
                this.console.logInfo(ret);
            } catch (final MatrixException e)  {
                this.console.logError(Messages.getString("MXAdapter.UpdateFailed", fileStr), e); //$NON-NLS-1$
            }
        }
    }

    /**
     * Extract the TCL update code for given <code>_file</code> from MX.
     *
     * @param _file     name of the update file for which the TCL update code
     *                  within MX must be extracted
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
}
