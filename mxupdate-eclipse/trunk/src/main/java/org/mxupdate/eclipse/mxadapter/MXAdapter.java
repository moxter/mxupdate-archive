/*
 * Copyright 2008-2010 The MxUpdate Team
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

package org.mxupdate.eclipse.mxadapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.mxupdate.eclipse.Messages;
import org.mxupdate.eclipse.adapter.IDeploymentAdapter;
import org.mxupdate.eclipse.adapter.IExportItem;
import org.mxupdate.eclipse.adapter.ISearchItem;
import org.mxupdate.eclipse.adapter.ITypeDefNode;
import org.mxupdate.eclipse.adapter.ITypeDefRoot;
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
     * Key of the preference if password could be stored.
     */
    public static final String PREF_STORE_PASSWORD = "storePassword"; //$NON-NLS-1$

    /**
     * Key of the update by file content preference.
     */
    public static final String PREF_UPDATE_FILE_CONTENT = "updateByFileContent"; //$NON-NLS-1$

    /**
     * Key name of the properties stored in the preferences.
     */
    private static final String PREF_PROPERTIES = "pluginProperties"; //$NON-NLS-1$

    /**
     * Key if the properties are configured in external property file.
     */
    public static final String PREF_EXTERNAL_CONFIGURED = "externalConfigured"; //$NON-NLS-1$

    /**
     * Key for the external property file.
     */
    public static final String PREF_PROP_FILE = "propertyFile"; //$NON-NLS-1$

    /**
     * Key of the property key host (URL) preference.
     */
    public static final String PREF_PROP_KEY_URL = "propKeyUrl"; //$NON-NLS-1$

    /**
     * Key of the property key user name preference.
     */
    public static final String PREF_PROP_KEY_NAME = "propKeyName"; //$NON-NLS-1$

    /**
     * Key of the property key password preference.
     */
    public static final String PREF_PROP_KEY_PASSWORD = "propKeyPassword"; //$NON-NLS-1$

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
     * Name of the key in the return map for the log message.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_LOG = "log";

    /**
     * Name of the key in the return map for the error message.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_ERROR = "error";

    /**
     * Name of the key in the return map for the exception.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_EXCEPTION = "exception";

    /**
     * Name of the key in the return map for the values.
     *
     * @see #prepareReturn(String, String, Exception, Object)
     */
    private static final String RETURN_KEY_VALUES = "values";

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
     * Map used to hold the images for MxUpdate files. The first key is the
     * file extension, the second key the file prefix.
     *
     * @see #initImageDescriptors()
     * @see #getImageDescriptor(IFile)
     */
    private final Map<String,Map<String,ImageDescriptor>> imageMap
            = new HashMap<String,Map<String,ImageDescriptor>>();

    /**
     * Mapping between type definition and related image descriptors.
     *
     * @see #initImageDescriptors()
     * @see #getImageDescriptor(String)
     */
    private final Map<String,ImageDescriptor> typeDef2Image = new HashMap<String,ImageDescriptor>();

    /**
     * Initializes the MX adapter.
     *
     * @param _preferences  preference store
     * @param _console      console used for logging purposes
     */
    public MXAdapter(final IPreferenceStore _preferences,
                     final Console _console)
    {
        this.preferences = _preferences;
        this.console = _console;
        this.initImageDescriptors();
    }

    /**
     * Initializes the image descriptors read from the plug in properties.
     *
     * @see #imageMap
     * @see #typeDef2Image
     */
    protected void initImageDescriptors()
    {
        final Properties properties = new Properties();
        final String propStr = this.preferences.getString("pluginProperties"); //$NON-NLS-1$
        if (propStr != null)  {
            final InputStream is = new ByteArrayInputStream(propStr.getBytes());
            try {
                properties.load(is);
            } catch (final IOException e) {
                this.console.logError("MxAdapter.ExceptionInitImageDescriptorsLoadPropertiesFailed", e);
            }
        }

        // extract all admin type names
        final Set<String> admins = new HashSet<String>();
        for (final Object keyObj : properties.keySet())  {
            final String key = keyObj.toString().replaceAll("\\..*", ""); //$NON-NLS-1$ //$NON-NLS-2$
            admins.add(key);
        }

        // prepare image cache
        for (final String admin : admins)  {
            final String prefix = properties.getProperty(admin + ".FilePrefix"); //$NON-NLS-1$
            final String suffix = properties.getProperty(admin + ".FileSuffix"); //$NON-NLS-1$
            final String iconStr = properties.getProperty(admin + ".Icon"); //$NON-NLS-1$

            final byte[] bin = Base64.decodeBase64(iconStr.getBytes());
            final InputStream in = new ByteArrayInputStream(bin);

            final ImageDescriptor imageDesriptor = ImageDescriptor.createFromImageData(new ImageData(in));

            // mapping between file prefix / extension and image
            Map<String,ImageDescriptor> mapPrefix = this.imageMap.get(suffix);
            if (mapPrefix == null)  {
                mapPrefix = new HashMap<String,ImageDescriptor>();
                this.imageMap.put(suffix, mapPrefix);
            }
            mapPrefix.put(prefix, imageDesriptor);

            // mapping between type definition and image
            this.typeDef2Image.put(admin, imageDesriptor);
        }
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
            final boolean connectAllowed;
            final String user;
            final String passwd;
            final String host;

            // is external configured
            if (this.preferences.getBoolean(MXAdapter.PREF_EXTERNAL_CONFIGURED))  {
                final String propFile = this.preferences.getString(MXAdapter.PREF_PROP_FILE);
                final String propHost = this.preferences.getString(MXAdapter.PREF_PROP_KEY_URL);
                final String propName = this.preferences.getString(MXAdapter.PREF_PROP_KEY_NAME);
                final String propPass = this.preferences.getString(MXAdapter.PREF_PROP_KEY_PASSWORD);

                this.console.logInfo(Messages.getString("MXAdapter.ReadExternalFile", propFile)); //$NON-NLS-1$
                final Properties extProps = new Properties();
                boolean read = false;
                try {
                    extProps.load(new FileInputStream(propFile));
                    read = true;
                } catch (final FileNotFoundException e) {
                    this.console.logError(Messages.getString("MXAdapter.ExceptionExternalFileOpenFailed"), e); //$NON-NLS-1$
                } catch (final IOException e) {
                    this.console.logError(Messages.getString("MXAdapter.ExceptionExternalFileOpenFailed"), e); //$NON-NLS-1$
                }

                if (!read)  {
                    connectAllowed = false;
                    host = null;
                    user = null;
                    passwd = null;
                } else if ((propName == null) || "".equals(propName) || (propPass == null) || "".equals(propPass))  {
                    host = extProps.getProperty(propHost);
                    final MXLoginPage loginPage = new MXLoginPage(host, this.preferences.getString(MXAdapter.PREF_NAME));
                    loginPage.open();
                    connectAllowed = loginPage.isOkPressed();
                    user = loginPage.getUserName();
                    passwd = loginPage.getPassword();
                } else  {
                    connectAllowed = true;
                    host = extProps.getProperty(propHost);
                    user = extProps.getProperty(propName);
                    passwd = extProps.getProperty(propPass);
                }

            // internal configured
            } else {
                host =  this.preferences.getString(MXAdapter.PREF_URL);
                if (!this.preferences.getBoolean(MXAdapter.PREF_STORE_PASSWORD))  {
                    final MXLoginPage loginPage = new MXLoginPage(host, this.preferences.getString(MXAdapter.PREF_NAME));
                    loginPage.open();
                    connectAllowed = loginPage.isOkPressed();
                    user = loginPage.getUserName();
                    passwd = loginPage.getPassword();
                } else  {
                    connectAllowed = true;
                    user =  this.preferences.getString(MXAdapter.PREF_NAME);
                    passwd =  this.preferences.getString(MXAdapter.PREF_PASSWORD);
                }
            }

            if (connectAllowed)  {

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
                final Map<?,?> bck = this.executeEncoded(new String[]{"Compile", String.valueOf(_compile)},
                                                         "Update",
                                                         new Object[]{"FileContents", files});
                this.console.logInfo((String) bck.get(MXAdapter.RETURN_KEY_LOG));
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
                final Map<?,?> bck = this.executeEncoded(new String[]{"Compile", String.valueOf(_compile)},
                                                         "Update",
                                                         new Object[]{"FileNames", fileNames});
                this.console.logInfo((String) bck.get(MXAdapter.RETURN_KEY_LOG));
            } catch (final Exception e)  {
                this.console.logError(Messages.getString("MXAdapter.ExceptionUpdateFailed", //$NON-NLS-1$
                                                         fileNames.toString()),
                                      e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * The type definition root is evaluated directly in MX and only converted
     * in the required format.
     */
    public ITypeDefRoot getTypeDefRoot()
    {
        Map<?,?> bck = null;
        try {
            bck = this.executeEncoded(null, "TypeDefTreeList", null);
        } catch (final IOException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionRootTypeDefFailed"), e); //$NON-NLS-1$
        } catch (final MatrixException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionRootTypeDefFailed"), e); //$NON-NLS-1$
        } catch (final ClassNotFoundException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionRootTypeDefFailed"), e); //$NON-NLS-1$
        }

        final ITypeDefRoot ret;
        if (bck == null)  {
            ret = null;
        } else if (bck.get(MXAdapter.RETURN_KEY_EXCEPTION) != null)  {
            ret = null;
            this.console.logError(Messages.getString("MXAdapter.ExceptionRootTypeDefFailed"), //$NON-NLS-1$
                                  (Exception) bck.get(MXAdapter.RETURN_KEY_EXCEPTION));
        } else  {
            final Map<?,?> treeMap = (Map<?,?>) bck.get(MXAdapter.RETURN_KEY_VALUES);
            ret = new ITypeDefRoot()  {
                private final Collection<ITypeDefNode> typeDefNodes = new ArrayList<ITypeDefNode>();
                public Collection<ITypeDefNode> getSubTypeDef()
                {
                    return this.typeDefNodes;
                }
            };
            this.appendSubNode(ret, (Map<?,?>) treeMap.get("All"), treeMap);
        }
        return ret;
    }

    /**
     * Appends to the <code>_node</code> all required / defined sub nodes.
     *
     * @param _node         node where sub nodes must be appended
     * @param _treeNode     current tree node which must be appended
     * @param _treeMap      map with all tree definitions from MX
     */
    protected void appendSubNode(final ITypeDefRoot _node,
                                 final Map<?,?> _treeNode,
                                 final Map<?,?> _treeMap)
    {
        final Collection<?> typeDefTreeList = (Collection<?>) _treeNode.get("TypeDefTreeList");
        if (typeDefTreeList != null)  {
            for (final Object subTreeNameObj : typeDefTreeList)  {
                if ((subTreeNameObj != null) && !"".equals(subTreeNameObj))  {
                    final Map<?,?> subTreeNode = (Map<?,?>) _treeMap.get(subTreeNameObj);
                    final String subLabel = (String) subTreeNode.get("Label");
                    final Collection<?> subTypeDefs = (Collection<?>) subTreeNode.get("TypeDefList");
                    final String[] typeDefArr;
                    if ((subTypeDefs != null) && !subTypeDefs.isEmpty())  {
                        typeDefArr = new String[subTypeDefs.size()];
                        int idx = 0;
                        for (final Object typeDefObj : subTypeDefs)  {
                            typeDefArr[idx++] = (String) typeDefObj;
                        }
                    } else  {
                        typeDefArr = new String[0];
                    }
                    final ITypeDefNode subNode = new ITypeDefNode() {
                        private final Collection<ITypeDefNode> typeDefNodes = new ArrayList<ITypeDefNode>();
                        public String getLabel()
                        {
                            return subLabel;
                        }
                        public Set<String> getTypeDefs()
                        {
                            return new HashSet<String>(Arrays.asList(typeDefArr));
                        }
                        public Collection<ITypeDefNode> getSubTypeDef()
                        {
                            return this.typeDefNodes;
                        }
                    };
                    this.appendSubNode(subNode, subTreeNode, _treeMap);
                    _node.getSubTypeDef().add(subNode);
                }
            }
        }
    }

    /**
     * Searches for configuration items within MX.
     *
     * @param _typeDefList  list with searched type definitions
     * @param _match        string for the names with must match
     * @return found search items
     */
    public List<ISearchItem> search(final Set<String> _typeDefList,
                                    final String _match)
    {
        Map<?,?> bck = null;
        try {
            bck = this.executeEncoded(null,
                                      "Search",
                                      new Object[]{"TypeDefList", _typeDefList,
                                                   "Match", _match});
        } catch (final IOException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionSearchFailed"), e); //$NON-NLS-1$
        } catch (final MatrixException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionSearchFailed"), e); //$NON-NLS-1$
        } catch (final ClassNotFoundException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionSearchFailed"), e); //$NON-NLS-1$
        }

        final List<ISearchItem> ret = new ArrayList<ISearchItem>();
        if (bck.get(MXAdapter.RETURN_KEY_EXCEPTION) != null)  {
            this.console.logError(Messages.getString("MXAdapter.ExceptionSearchFailed"), //$NON-NLS-1$
                                  (Exception) bck.get(MXAdapter.RETURN_KEY_EXCEPTION));
        } else  {
            final List<?> values = (List<?>) bck.get(MXAdapter.RETURN_KEY_VALUES);
            for (final Object valueObj : values)  {
                final Map<?,?> value = (Map<?,?>) valueObj;
                ret.add(new ISearchItem() {
                    public String getFileName()
                    {
                        return (String) value.get("FileName");
                    }
                    public String getFilePath()
                    {
                        return (String) value.get("FilePath");
                    }
                    public String getName()
                    {
                        return (String) value.get("Name");
                    }
                    public String getTypeDef()
                    {
                        return (String) value.get("TypeDef");
                    }
                });
            }
        }
        return ret;
    }

    /**
     * Extract the TCL update code for given <code>_file</code> from MX.
     *
     * @param _file     name of the update file for which the TCL update code
     *                  within MX must be extracted
     * @return configuration item update code for given <code>_file</code>
     */
    public IExportItem export(final IFile _file)
    {
        Map<?,?> bck = null;
        try {
            bck = this.executeEncoded(null,
                                      "Export",
                                      new Object[]{"FileName", _file.toString()});
        } catch (final IOException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), e); //$NON-NLS-1$
        } catch (final MatrixException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), e); //$NON-NLS-1$
        } catch (final ClassNotFoundException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), e); //$NON-NLS-1$
        }

        final IExportItem ret;
        if (bck.get(MXAdapter.RETURN_KEY_EXCEPTION) != null)  {
            ret = null;
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), //$NON-NLS-1$
                                  (Exception) bck.get(MXAdapter.RETURN_KEY_EXCEPTION));
        } else  {
            final Map<?,?> value = (Map<?,?>) bck.get(MXAdapter.RETURN_KEY_VALUES);
            ret = new IExportItem() {
                public String getFileName()
                {
                    return (String) value.get("FileName");
                }
                public String getFilePath()
                {
                    return (String) value.get("FilePath");
                }
                public String getName()
                {
                    return (String) value.get("Name");
                }
                public String getTypeDef()
                {
                    return (String) value.get("TypeDef");
                }
                public String getContent()
                {
                    return (String) value.get("Code");
                }
            };
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public IExportItem export(final String _typeDef,
                              final String _item)
    {
        Map<?,?> bck = null;
        try {
            bck = this.executeEncoded(null,
                                      "Export",
                                      new Object[]{"TypeDef", _typeDef,
                                                   "Name", _item});
        } catch (final IOException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), e); //$NON-NLS-1$
        } catch (final MatrixException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), e); //$NON-NLS-1$
        } catch (final ClassNotFoundException e) {
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), e); //$NON-NLS-1$
        }

        final IExportItem ret;
        if (bck.get(MXAdapter.RETURN_KEY_EXCEPTION) != null)  {
            ret = null;
            this.console.logError(Messages.getString("MXAdapter.ExceptionExportFailed"), //$NON-NLS-1$
                                  (Exception) bck.get(MXAdapter.RETURN_KEY_EXCEPTION));
        } else  {
            final Map<?,?> value = (Map<?,?>) bck.get(MXAdapter.RETURN_KEY_VALUES);
            ret = new IExportItem() {
                public String getFileName()
                {
                    return (String) value.get("FileName");
                }
                public String getFilePath()
                {
                    return (String) value.get("FilePath");
                }
                public String getName()
                {
                    return (String) value.get("Name");
                }
                public String getTypeDef()
                {
                    return (String) value.get("TypeDef");
                }
                public String getContent()
                {
                    return (String) value.get("Code");
                }
            };
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @see #imageMap
     */
    public ImageDescriptor getImageDescriptor(final IFile _file)
    {
        final String name = _file.getName();

        ImageDescriptor ret = null;
        for (final Map.Entry<String,Map<String,ImageDescriptor>> suffixEntry : this.imageMap.entrySet())  {
            if (name.endsWith(suffixEntry.getKey()))  {
                for (final Map.Entry<String, ImageDescriptor> entry : suffixEntry.getValue().entrySet())  {
                    if (name.startsWith(entry.getKey()))  {
                        ret = entry.getValue();
                        break;
                    }
                }
                break;
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @see #typeDef2Image
     */
    public ImageDescriptor getImageDescriptor(final String _typeDef)
    {
        return this.typeDef2Image.get(_typeDef);
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
     * Calls given <code>_method</code> in of the MxUpdate eclipse plug-in
     * dispatcher. The MX context {@link #mxContext} is connected to the
     * database if not already done.
     *
     * @param _method       method of the called <code>_jpo</code>
     * @param _arguments    list of all parameters for the <code>_jpo</code>
     *                      which are automatically encoded encoded
     * @return returned value from the called <code>_jpo</code>
     * @throws IOException      if the parameter could not be encoded
     * @throws MatrixException  if the called <code>_jpo</code> throws an
     *                          exception
     * @throws ClassNotFoundException if the class which is decoded from the
     *                          returned string value could not be found
     * @see #mxContext
     * @see #connect()
     */
    protected Map<?,?> executeEncoded(final String[] _parameters,
                                      final String _method,
                                      final Object[] _arguments)
        throws IOException, MatrixException, ClassNotFoundException
    {
        if (!this.connected)  {
            this.connect();
        }

        // prepare parameters in a map
        final Map<String,String> parameters;
        if ((_parameters == null) || (_parameters.length == 0))  {
            parameters = null;
        } else  {
            parameters = new HashMap<String,String>();
            for (int idx = 0; idx < _parameters.length; )  {
                parameters.put(_parameters[idx++], _parameters[idx++]);
            }
        }

        // prepare arguments in a map
        final Map<String,Object> arguments;
        if ((_arguments == null) || (_arguments.length == 0))  {
            arguments = null;
        } else  {
            arguments = new HashMap<String,Object>();
            for (int idx = 0; idx < _arguments.length; )  {
                arguments.put((String) _arguments[idx++], _arguments[idx++]);
            }
        }

        // prepare MQL statement with encoded parameters
        final StringBuilder cmd = new StringBuilder()
            .append("exec prog ").append("org.mxupdate.plugin.Dispatcher \"")
            .append(this.encode(parameters)).append("\" \"")
            .append(this.encode(_method)).append("\" \"")
            .append(this.encode(arguments)).append("\"");

        // execute MQL command
        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(this.mxContext, cmd.toString());
        if ((mql.getError() != null) && !"".equals(mql.getError()))  { //$NON-NLS-1$
            throw new MatrixException(mql.getError());
        }

        return this.<Map<?,?>>decode(mql.getResult());
    }

    /**
     * Encodes given <code>_object</code> to a string with <b>base64</b>.
     *
     * @param _object   object to encode
     * @return encoded string
     * @throws IOException if encode failed
     */
    protected String encode(final Object _object)
        throws IOException
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(_object);
        oos.close();
        return new String(Base64.encodeBase64(out.toByteArray()));
    }

    /**
     * Decodes given string value to an object of given type
     * <code>&lt;T&gt;</code>. First the string is <b>base64</b> decoded, then
     * the object instance is extracted from the decoded bytes via the Java
     * &quot;standard&quot; feature of the {@link ObjectInputStream}.
     *
     * @param <T>   type of the object which must be decoded
     * @param _arg  string argument with encoded instance of
     *              <code>&lt;T&gt;</code>
     * @return decoded object instance of given type <code>&lt;T&gt;</code>
     * @throws IOException              if the value could not be decoded,
     *                                  the decoder stream could not be
     *                                  opened or the argument at given
     *                                  <code>_index</code> is not defined
     * @throws ClassNotFoundException   if the object itself could not be read
     *                                  from decoder stream
     */
    @SuppressWarnings("unchecked")
    protected final <T> T decode(final String _arg)
        throws IOException, ClassNotFoundException
    {
        final byte[] bytes = Base64.decodeBase64(_arg.getBytes());
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        final ObjectInputStream ois = new ObjectInputStream(in);
        final T ret = (T) ois.readObject();
        ois.close();
        return ret;
    }
}
