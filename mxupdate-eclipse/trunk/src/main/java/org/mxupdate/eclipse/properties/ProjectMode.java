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

package org.mxupdate.eclipse.properties;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.mxupdate.eclipse.Messages;
import org.mxupdate.eclipse.adapter.IDeploymentAdapter;
import org.mxupdate.eclipse.console.Console;
import org.mxupdate.eclipse.mxadapter.MXAdapter;
import org.mxupdate.eclipse.mxadapter.connectors.IConnector;
import org.mxupdate.eclipse.mxadapter.connectors.SSHConnector;
import org.mxupdate.eclipse.mxadapter.connectors.URLConnector;

/**
 * Enumeration to differ the modes of projects.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public enum ProjectMode
{
    /**
     * The mode is now defined and unknown.
     */
    UNKNOWN {
        /**
         * {@inheritDoc}
         * Throws always an error because the mode is unknown and no adapter is
         * defined.
         */
        @Override()
        public IDeploymentAdapter initAdapter(final IProject _project,
                                          final ProjectProperties _properties,
                                          final Console _console)
            throws IOException
        {
            throw new Error("not implemented");
        }

        /**
         * {@inheritDoc}
         * Throws always an error because the mode is unknown and no connector
         * is defined.
         */
        @Override()
        public IConnector initConnector(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
            throws Exception
        {
            throw new Error("not implemented");
        }

        /**
         * Appends nothing because unknown mode contains no settings.
         * {@inheritDoc}
         */
        @Override()
        public void createContent(final Composite _composite,
                                  final ProjectProperties _properties)
        {
        }
    },

    /**
     * Classic, MXUpdate Eclipse Plug-In connects via URL or via shared
     * library.
     */
    MXUPDATE_VIA_URL
    {
        /** Prefix used for property keys. */
        private final String prefix = "MxUpdateViaURL."; //$NON-NLS-1$

        /** Name of the property key for the Java path. */
        private final String propJavaPath = this.prefix + "JavaPath"; //$NON-NLS-1$

        /** Default value for the Java path. */
        private final String valJavaPath = "java"; //$NON-NLS-1$

        /** Name of the property key for the path of the MX Jar library. */
        private final String propMxJarLibraryPath = this.prefix + "MxJarLibraryPath"; //$NON-NLS-1$

        /** Name of the property key for the URL to the MX server. */
        private final String propUrl = this.prefix + "URL"; //$NON-NLS-1$

        /** Name of the property key for the MX user name. */
        private final String propUser = this.prefix + "UserName"; //$NON-NLS-1$

        /** Name of the property key for the password. */
        private final String propPassword = this.prefix+ "Password"; //$NON-NLS-1$

        /** Name of the property key if the password could be saved. */
        private final String propSavePassword = this.prefix + "SavePassword"; //$NON-NLS-1$

        /** Default value for the flag if the password could be saved. */
        private final boolean valSavePassword = false;

        /**
         * Name of the property key if the update is done with the file
         * content.
         */
        private final String propUpdateByFileContent = this.prefix + "UpdateByFileContent"; //$NON-NLS-1$

        /**
         * {@inheritDoc}
         */
        @Override()
        public void createContent(final Composite _parent,
                                  final ProjectProperties _properties)
        {
            // Java instance
            final Group javaGroup = FieldUtil.createGroup(_parent, this.prefix + "JavaGroup"); //$NON-NLS-1$
            FieldUtil.addFileField(javaGroup, _properties, this.propJavaPath, this.valJavaPath);
            FieldUtil.addFileField(javaGroup, _properties, this.propMxJarLibraryPath, "", "*.jar", "*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            // MX connection settings
            final Group mxGroup = FieldUtil.createGroup(_parent, this.prefix + "MxGroup"); //$NON-NLS-1$
            FieldUtil.addStringField(mxGroup, _properties, this.propUrl, ""); //$NON-NLS-1$
            FieldUtil.addStringField(mxGroup, _properties, this.propUser, ""); //$NON-NLS-1$
            FieldUtil.addPasswordField(mxGroup, _properties, this.propPassword, this.propSavePassword);

            FieldUtil.addBooleanField(_parent, _properties, this.propUpdateByFileContent, true);
        }

        /**
         * {@inheritDoc}
         * @see MXAdapter
         */
        @Override()
        public IDeploymentAdapter initAdapter(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
        {
            return new MXAdapter(_project, _properties, _console);
        }

        /**
         * {@inheritDoc}
         * The {@link URLConnector} is initialized. Depending on the
         * {@link #propSavePassword} the MX user name / password is asked from
         * the user.
         *
         * @see SSHConnector
         */
        @Override()
        public IConnector initConnector(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
            throws Exception
        {
            final String mxURL = _properties.getString(this.propUrl, "");

            // MX authentication
            final String mxUser;
            final String mxPasswd;
            if (_properties.getBoolean(this.propSavePassword, this.valSavePassword))  {
                mxUser = _properties.getString(this.propUser, null);
                mxPasswd = _properties.getPassword(this.propPassword);
            } else  {
                final AuthenticationDialog loginPage = new AuthenticationDialog(
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MxGroup")),
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.propUser)),
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.propPassword)),
                        _properties.getString(this.propUser, null));
                loginPage.open();
                if (!loginPage.isOkPressed())  {
                    throw new Exception(Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("NotMXAuthenticated")));
                }
                mxUser = loginPage.getUserName();
                mxPasswd = loginPage.getPassword();
            }

            _console.logInfo(Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("ConnectTo"), mxURL)); //$NON-NLS-1$

            return new URLConnector(
                    _project,
                    _properties.getString(this.propJavaPath, this.valJavaPath),
                    _properties.getString(this.propMxJarLibraryPath, ""), //$NON-NLS-1$
                    mxURL, mxUser, mxPasswd,
                    _properties.getBoolean(this.propUpdateByFileContent, true));
        }

        /**
         * {@inheritDoc}
         *
         * @return <code>null</code> if property keys {@link #propJavaPath},
         *         {@link #propMxJarLibraryPath} and {@link #propUser} are not
         *         empty
         */
        @Override()
        public String isValid(final ProjectProperties _properties)
        {
            final String ret;

            if (_properties.getString(this.propJavaPath, this.valJavaPath).isEmpty())  {
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MissingJavaPath")); //$NON-NLS-1$
            } else if (_properties.getString(this.propMxJarLibraryPath, "").isEmpty())  { //$NON-NLS-1$
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MissingMxJarLibraryPath")); //$NON-NLS-1$
            } else  if (_properties.getString(this.propUser, "").isEmpty())  { //$NON-NLS-1$
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MissingUserName")); //$NON-NLS-1$
            } else  {
                ret = null;
            }
            return ret;
        }
    },

    /**
     * Classic with external definition, means that the settings for the MX
     * connection are defined in a property file.
     */
    MXUPDATE_VIA_URL_WITH_PROPERTY_FILE
    {
        private final String prefix = "MxUpdateViaURLWithPropertyFile."; //$NON-NLS-1$

        private final String prefUrl = prefix + "KeyURL"; //$NON-NLS-1$

        private final String prefName = prefix + "KeyUserName"; //$NON-NLS-1$

        private final String prefPassword = prefix+ "KeyPassword"; //$NON-NLS-1$

        private final String prefSavePassword = prefix + "SavePassword"; //$NON-NLS-1$

        private final String prefUpdateByFileContent = prefix + "UpdateByFileContent"; //$NON-NLS-1$

        @Override()
        public void createContent(final Composite _parent,
                                  final ProjectProperties _properties)
        {
/*
                MXPropertyPage.addTextField(_parent,
                                            _properties,
                                            MXAdapter.PREF_PROP_FILE,
                                            Messages.getString("MXPreferencePage.PropertyFile"), //$NON-NLS-1$
                                            false);
            this.fileEditor = new FileFieldEditor(
                    MXAdapter.PREF_PROP_FILE,
                    Messages.getString("MXPreferencePage.PropertyFile"), //$NON-NLS-1$
                    composite);
            this.fileEditor.setFileExtensions(new String[]{"*.properties", "*"}); //$NON-NLS-1$
*/
            FieldUtil.addStringField(_parent, _properties, this.prefUrl, ""); //$NON-NLS-1$
            FieldUtil.addStringField(_parent, _properties, this.prefName, ""); //$NON-NLS-1$
            FieldUtil.addStringField(_parent, _properties, this.prefPassword, ""); //$NON-NLS-1$
            FieldUtil.addBooleanField(_parent, _properties, this.prefSavePassword, false);
            FieldUtil.addBooleanField(_parent, _properties,this.prefUpdateByFileContent, true);
        }

        /**
         * {@inheritDoc}
         * @see MXAdapter
         */
        @Override()
        public IDeploymentAdapter initAdapter(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
        {
            return new MXAdapter(_project, _properties, _console);
        }

        @Override()
        public IConnector initConnector(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
        throws Exception
        {
            return null;
        }
    },

    /**
     * MxUpdate Plug-In uses MQL on a SSH server.
     */
    MXUPDATE_SSH_MQL
    {
        /** Prefix used for property keys. */
        private final String prefix = "MxUpdateViaSSHMQL."; //$NON-NLS-1$

        /** Name of the property key for the name of the SSH server. */
        private final String propSSHServer = this.prefix + "SSHServer"; //$NON-NLS-1$

        /** Name of the property key for the port of the SSH server. */
        private final String propSSHPort = this.prefix + "SSHPort"; //$NON-NLS-1$

        /** Default value for the SSH port. */
        private final int valSSHPort = 22;

        /** Name of the property key for the name of the SSH user. */
        private final String propSSHUser = this.prefix + "SSHUser"; //$NON-NLS-1$

        /** Name of the property key for the password of the SSH user. */
        private final String propSSHPassword = this.prefix + "SSHPassword"; //$NON-NLS-1$

        /** Name of the property key for the flag if the password is saved. */
        private final String propSSHSavePassword = this.prefix + "SSHSavePassword"; //$NON-NLS-1$

        /** Default value for flag if the SSH password could be saved. */
        private final boolean valSSHSavePassword = false;

        /**
         * Name of the property key for the path of the MQL command on the SSH
         * server.
         */
        private final String propMXMQLPath = this.prefix + "MXMQLPath"; //$NON-NLS-1$

        /** Default value for the MQL path. */
        private final String valMXMQLPath = "mql"; //$NON-NLS-1$

        /** Name of the property key for the MX user name. */
        private final String propMXUserName = this.prefix + "MXUserName"; //$NON-NLS-1$

        /** Name of the property key for the MX password. */
        private final String propMXPassword = this.prefix+ "MXPassword"; //$NON-NLS-1$

        /** Name of the property key if the password could be saved. */
        private final String propMXSavePassword = this.prefix + "MXSavePassword"; //$NON-NLS-1$

        /** Default value for flag if the MX password could be saved. */
        private final boolean valMXSavePassword = false;

        /**
         * Name of the property for the flag if the MX communication is
         * logged.
         */
        private final String propMXLog = this.prefix + "MXLog";

        /** Default value for the MX communication logging. */
        private final boolean valMXLog = false;

        /**
         * Name of the property key if the update is done with the file
         * content.
         */
        private final String prefMXUpdateByFileContent = this.prefix + "MXUpdateByFileContent"; //$NON-NLS-1$

        /** Default value if the update is done with the file content. */
        private final boolean valMXUpdateByFileContent = true;

        /**
         * {@inheritDoc}
         */
        @Override()
        public void createContent(final Composite _parent,
                                  final ProjectProperties _properties)
        {
            // SSH connection settings
            final Group sshGroup = FieldUtil.createGroup(_parent,  this.prefix + "SSHGroup"); //$NON-NLS-1$
            FieldUtil.addSSHField(sshGroup, _properties, this.propSSHServer, this.propSSHPort, this.valSSHPort);
            FieldUtil.addStringField(sshGroup, _properties, this.propSSHUser, ""); //$NON-NLS-1$
            FieldUtil.addPasswordField(sshGroup, _properties, this.propSSHPassword, this.propSSHSavePassword);

            // MX connection settings
            final Group mxGroup = FieldUtil.createGroup(_parent,  this.prefix + "MXGroup"); //$NON-NLS-1$
            FieldUtil.addStringField(mxGroup, _properties, this.propMXMQLPath, this.valMXMQLPath);
            FieldUtil.addStringField(mxGroup, _properties, this.propMXUserName, ""); //$NON-NLS-1$
            FieldUtil.addPasswordField(mxGroup, _properties, this.propMXPassword, this.propMXSavePassword);

            FieldUtil.addBooleanField(_parent, _properties, this.propMXLog, this.valMXLog);
            FieldUtil.addBooleanField(_parent, _properties, this.prefMXUpdateByFileContent, this.valMXUpdateByFileContent);
        }

        /**
         * {@inheritDoc}
         *
         * @return <code>null</code> if property keys {@link #propSSHServer},
         *         {@link #propSSHUser}, {@link #propMXMQLPath} and
         *         {@link #propMXUserName} are not empty and
         *         {@link #propSSHPort} is between 0 and 65535;
         *         otherwise message with error text
         */
        @Override()
        public String isValid(final ProjectProperties _properties)
        {
            final String ret;
            if (_properties.getString(this.propSSHServer, "").isEmpty())  { //$NON-NLS-1$
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MissingSSHServer")); //$NON-NLS-1$
            } else if (_properties.isWrong(this.propSSHPort))  {
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("WrongSSHPort")); //$NON-NLS-1$
            } else if (_properties.getInteger(this.propSSHPort, this.valSSHPort) < 0)  {
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("SSHPortToLow"), //$NON-NLS-1$
                                         _properties.getInteger(this.propSSHPort, this.valSSHPort));
            } else if (_properties.getInteger(this.propSSHPort, this.valSSHPort) > 65535)  {
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("SSHPortToBig"), //$NON-NLS-1$
                                         _properties.getInteger(this.propSSHPort, this.valSSHPort));
            } else if (_properties.getString(this.propSSHUser, "").isEmpty())  { //$NON-NLS-1$
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MissingSSHUser")); //$NON-NLS-1$
            } else if (_properties.getString(this.propMXMQLPath, this.valMXMQLPath).isEmpty())  {
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MissingMXMQLPath")); //$NON-NLS-1$
            } else if (_properties.getString(this.propMXUserName, "").isEmpty())  { //$NON-NLS-1$
                ret = Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MissingMXUserName")); //$NON-NLS-1$
            } else  {
                ret = null;
            }
            return ret;
        }

        /**
         * {@inheritDoc}
         * @see MXAdapter
         */
        @Override()
        public IDeploymentAdapter initAdapter(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
        {
            return new MXAdapter(_project, _properties, _console);
        }

        /**
         * {@inheritDoc}
         * The {@link SSHConnector} is initialized. Depending on the
         * {@link #propSSHSavePassword} and {@link #propMXSavePassword} the SSH
         * user name / password or the MX user name / password is asked.
         *
         * @see SSHConnector
         */
        @Override()
        public IConnector initConnector(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
            throws Exception
        {
            // SSH authentication
            final String sshServer = _properties.getString(this.propSSHServer, null);
            final int sshPort = _properties.getInteger(this.propSSHPort, this.valSSHPort);
            final String sshUser;
            final String sshPasswd;
            if (_properties.getBoolean(this.propSSHSavePassword, this.valSSHSavePassword))  {
                sshUser = _properties.getString(this.propSSHUser, null);
                sshPasswd = _properties.getPassword(this.propSSHPassword);
            } else  {
                final AuthenticationDialog loginPage = new AuthenticationDialog(
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("SSHGroup")),
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.propSSHUser)),
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.propSSHPassword)),
                        _properties.getString(this.propSSHUser, null));
                loginPage.open();
                if (!loginPage.isOkPressed())  {
                    throw new Exception(Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("NotSSHAuthenticated")));
                }
                sshUser = loginPage.getUserName();
                sshPasswd = loginPage.getPassword();
            }

            // MX authentication
            final String mxUser;
            final String mxPasswd;
            if (_properties.getBoolean(this.propMXSavePassword, this.valMXSavePassword))  {
                mxUser = _properties.getString(this.propMXUserName, null);
                mxPasswd = _properties.getPassword(this.propMXPassword);
            } else  {
                final AuthenticationDialog loginPage = new AuthenticationDialog(
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("MXGroup")),
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.propMXUserName)),
                        Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.propMXPassword)),
                        _properties.getString(this.propMXUserName, null));
                loginPage.open();
                if (!loginPage.isOkPressed())  {
                    throw new Exception(Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("NotMXAuthenticated")));
                }
                mxUser = loginPage.getUserName();
                mxPasswd = loginPage.getPassword();
            }

            _console.logInfo(Messages.getString(new StringBuilder(ProjectProperties.MSG_PREFIX).append(this.prefix).append("ConnectTo"), //$NON-NLS-1$
                                                sshServer, sshPort));

            return new SSHConnector(
                    sshServer, sshPort, sshUser, sshPasswd,
                    _properties.getString(this.propMXMQLPath, this.valMXMQLPath),
                    mxUser, mxPasswd,
                    _properties.getBoolean(this.propMXLog, this.valMXLog),
                    _properties.getBoolean(this.prefMXUpdateByFileContent, this.valMXUpdateByFileContent));
        }
    },

    MXUPDATE_SSH_MQL_WITH_PROPERTY_FILE
    {
        @Override()
        public void createContent(final Composite _composite,
                                  final ProjectProperties _properties)
        {

        }

        /**
         * {@inheritDoc}
         * @see MXAdapter
         */
        @Override()
        public IDeploymentAdapter initAdapter(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
        {
            return new MXAdapter(_project, _properties, _console);
        }

        @Override()
        public IConnector initConnector(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
        throws Exception
        {
            return null;
        }
    },

    MXUPDATE_WITH_PROPERTY_FILE
    {
        @Override()
        public void createContent(final Composite _composite,
                                  final ProjectProperties _properties)
        {

        }

        /**
         * {@inheritDoc}
         * @see MXAdapter
         */
        @Override()
        public IDeploymentAdapter initAdapter(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
        {
            return new MXAdapter(_project, _properties, _console);
        }

        @Override()
        public IConnector initConnector(final IProject _project,
                                              final ProjectProperties _properties,
                                              final Console _console)
            throws Exception
        {
            return null;
        }
    };

    /**
     * Appends all specific fields needed to configure defined mode.
     *
     * @param _composite        parent composite where the content must be
     *                          appended
     * @param _properties       project specific properties
     */
    public abstract void createContent(final Composite _composite,
                                       final ProjectProperties _properties);

    /**
     * Initialize the adapter depending on the eclipse <code>_project</code>
     * for given <code>_properties</code>.
     *
     * @param _project      eclipse project
     * @param _properties   related properties of the eclipse
     *                      <code>_project</code>
     * @param _console      logging console
     * @return initialized adapter
     * @throws Exception if adapter could not be initialized
     */
    public abstract IDeploymentAdapter initAdapter(final IProject _project,
                                                   final ProjectProperties _properties,
                                                   final Console _console)
        throws Exception;

    /**
     * Initialized the connector and establish the connection to the database.
     *
     * @param _project      eclipse project
     * @param _properties   related properties of the eclipse
     *                      <code>_project</code>
     * @param _console      logging console
     * @return initialized connector
     * @throws Exception if connector could not be initialized
     */
    public abstract IConnector initConnector(final IProject _project,
                                             final ProjectProperties _properties,
                                             final Console _console)
        throws Exception;

    /**
     * Checks if the property values for current mode are valid.
     *
     * @param _properties   related property values
     * @return <i>true</i> if property values are valid; otherwise <i>false</i>
     */
    public String isValid(final ProjectProperties _properties)
    {
        return "";
    }

    /**
     * Initialize the adapter depending on the eclipse <code>_project</code>.
     *
     * @param _project      eclipse project for which the adapter is searched
     * @param _console      logging console
     * @return initialized adapter
     * @throws Exception if adapter could not be initialized
     */
    public static IDeploymentAdapter initAdapter(final IProject _project,
                                                 final Console _console)
        throws Exception
    {
        // load properties
        final ProjectProperties properties = new ProjectProperties(_project);
        // and connect
        return (properties.getMode() == ProjectMode.UNKNOWN)
               ? null
               : properties.getMode().initAdapter(_project, properties, _console);
    }

    /**
     * Initialized the connector and establish the connection to the database.
     *
     * @param _project      eclipse project
     * @param _console      logging console
     * @return initialized connector
     * @throws Exception if connector could not be initialized
     */
    public IConnector initConnector(final IProject _project,
                                    final Console _console)
        throws Exception
    {
            // load properties
            final ProjectProperties properties = new ProjectProperties(_project);
            // and connect
            return properties.getMode().initConnector(_project, properties, _console);
    }
}
