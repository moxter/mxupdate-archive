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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mxupdate.eclipse.Activator;
import org.mxupdate.eclipse.Messages;

/**
 * Common preference page for the MxUpdate eclipse plug-in to define URL, name
 * and password for the MX context.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MXPreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{
    /**
     * String field editor for the host URL.
     */
    private StringFieldEditor host;

    /**
     * String field editor for the user name.
     */
    private StringFieldEditor userName;

    /**
     * Password field editor.
     */
    private PasswordFieldEditor password;

    /**
     * Flag that the password must be stored.
     */
    private BooleanFieldEditor storePassword;

    /**
     * Flag that the update is done with the file content.
     */
    private BooleanFieldEditor updateByFileContent;

    /**
     * File selector for the property file.
     */
    private FileFieldEditor fileEditor;

    /**
     * Editor for the host (URL) property key.
     */
    private StringFieldEditor propKeyHost;

    /**
     * Editor for the user name property key.
     */
    private StringFieldEditor propKeyUserName;

    /**
     * Editor for the password property key.
     */
    private StringFieldEditor propKeyPassword;

    /**
     * Initialize the layout of the preference (defined as grid) and defines
     * the preference store from the plug-in activator {@link Activator}.
     */
    public MXPreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
        this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /**
     *
     * @see FieldEditorPreferencePage#createFieldEditors()
     */
    @Override()
    protected void createFieldEditors()
    {
        final Composite parent = this.getFieldEditorParent();

        this.host = new StringFieldEditor(
                MXAdapter.PREF_URL,
                Messages.getString("MXPreferencePage.Host"), //$NON-NLS-1$
                parent);
        this.addField(this.host);

        this.userName = new StringFieldEditor(
                MXAdapter.PREF_NAME,
                Messages.getString("MXPreferencePage.UserName"), //$NON-NLS-1$
                parent);
        this.addField(this.userName);

        this.password = new PasswordFieldEditor(
                MXAdapter.PREF_PASSWORD,
                Messages.getString("MXPreferencePage.Password"), //$NON-NLS-1$
                parent);
        this.addField(this.password);

        this.storePassword = new BooleanFieldEditor(
                MXAdapter.PREF_STORE_PASSWORD,
                Messages.getString("MXPreferencePage.StorePassword"), //$NON-NLS-1$
                parent);
        this.addField(this.storePassword);

        this.updateByFileContent = new BooleanFieldEditor(
                MXAdapter.PREF_UPDATE_FILE_CONTENT,
                Messages.getString("MXPreferencePage.UpdateByFileContent"), //$NON-NLS-1$
                parent);
        this.addField(this.updateByFileContent);

        final BooleanFieldEditor externalConfigured = new BooleanFieldEditor(
                MXAdapter.PREF_EXTERNAL_CONFIGURED,
                Messages.getString("MXPreferencePage.ExternalConfigured"), //$NON-NLS-1$
                parent)
        {
            /**
             * Called if the preference page is called the first time.
             */
            @Override()
            protected void doLoad()
            {
                super.doLoad();
                MXPreferencePage.this.updateDependencies(this.getBooleanValue());
            }
            /**
             * Called if the user (de-) selects the external configuration
             * flag.
             *
             * @param _oldValue     old value
             * @param _newValue     new value
             */
            @Override()
            protected void valueChanged(final boolean _oldValue,
                                        final boolean _newValue)
            {
                super.valueChanged(_oldValue, _newValue);
                MXPreferencePage.this.updateDependencies(_newValue);
            }

        };
        this.addField(externalConfigured);

        this.fileEditor = new FileFieldEditor(
                MXAdapter.PREF_PROP_FILE,
                Messages.getString("MXPreferencePage.PropertyFile"), //$NON-NLS-1$
                parent);
        this.fileEditor.setFileExtensions(new String[]{"*.properties", "*"}); //$NON-NLS-1$
        this.addField(this.fileEditor);

        this.propKeyHost = new StringFieldEditor(
                MXAdapter.PREF_PROP_KEY_URL,
                Messages.getString("MXPreferencePage.PropKeyHost"), //$NON-NLS-1$
                parent);
        this.addField(this.propKeyHost);

        this.propKeyUserName = new StringFieldEditor(
                MXAdapter.PREF_PROP_KEY_NAME,
                Messages.getString("MXPreferencePage.PropKeyUserName"), //$NON-NLS-1$
                parent);
        this.addField(this.propKeyUserName);

        this.propKeyPassword = new StringFieldEditor(
                MXAdapter.PREF_PROP_KEY_PASSWORD,
                Messages.getString("MXPreferencePage.PropKeyPassword"), //$NON-NLS-1$
                parent);
        this.addField(this.propKeyPassword);
    }

    /**
     * Enables / disables the preferences depending on the flag if properties
     * from external files are read.
     *
     * @param _enable   <i>true</i> if configured in external file
     */
    protected void updateDependencies(final boolean _enable)
    {
        final Composite parent = this.getFieldEditorParent();

        // enable / disable internal configured
        this.host.setEnabled(!_enable, parent);
        this.userName.setEnabled(!_enable, parent);
        this.password.setEnabled(!_enable, parent);
        this.storePassword.setEnabled(!_enable, parent);
        this.updateByFileContent.setEnabled(!_enable, parent);
        // enable / disable external configured
        this.fileEditor.setEnabled(_enable, parent);
        this.propKeyHost.setEnabled(_enable, parent);
        this.propKeyUserName.setEnabled(_enable, parent);
        this.propKeyPassword.setEnabled(_enable, parent);
    }

    /**
     * @param _workbench    workbench which must be initialized
     * @see IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(final IWorkbench _workbench)
    {
    }

    /**
     * A field editor for a password type preference. This is essentially
     * a StringFieldEditor, but will replace each character in the input
     * box with an '*'.
     */
    private static class PasswordFieldEditor
        extends StringFieldEditor
    {
        /**
         * The text field, or <code>null</code> if none.
         */
        private Text textField;

        /**
         * Creates a password field editor of unlimited width.
         *
         * @param _name         name of the preference this field editor works
         *                      on
         * @param _labelText    label text of the field editor
         * @param _parent       parent of the field editor's control
         */
        public PasswordFieldEditor(final String _name,
                                   final String _labelText,
                                   final Composite _parent)
        {
            super(_name, _labelText, _parent);
        }

        /**
         * Returns this field editor's text control. The control is created if
         * it does not yet exist
         *
         * @param _parent       parent composite
         * @return text control
         */
        @Override()
        public Text getTextControl(final Composite _parent)
        {
            if (this.textField == null) {
                this.textField = new Text(_parent, SWT.PASSWORD | SWT.SINGLE | SWT.BORDER);
                this.textField.setFont(_parent.getFont());
                this.textField.addKeyListener(new KeyAdapter()  {
                    @Override
                    public void keyReleased(final KeyEvent _event)
                    {
                        MXPreferencePage.PasswordFieldEditor.this.valueChanged();
                    }
                });
                this.textField.addDisposeListener(new DisposeListener()  {
                    public void widgetDisposed(final DisposeEvent _event)
                    {
                        MXPreferencePage.PasswordFieldEditor.this.textField = null;
                    }
                });
            } else  {
                this.checkParent(this.textField, _parent);
            }
            return this.textField;
        }
    }
}
