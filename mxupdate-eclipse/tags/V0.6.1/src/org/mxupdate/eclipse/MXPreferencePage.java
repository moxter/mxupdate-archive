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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = this.getFieldEditorParent();

        this.addField(new StringFieldEditor(
                MXAdapter.PREF_URL,
                Messages.getString("MXPreferencePage.Host"), //$NON-NLS-1$
                parent));

        this.addField(new StringFieldEditor(
                MXAdapter.PREF_NAME,
                Messages.getString("MXPreferencePage.UserName"), //$NON-NLS-1$
                parent));

        this.addField(new PasswordFieldEditor(
                MXAdapter.PREF_PASSWORD,
                Messages.getString("MXPreferencePage.Password"), //$NON-NLS-1$
                parent));

        this.addField(new BooleanFieldEditor(
                MXAdapter.PREF_STORE_PASSWORD,
                Messages.getString("MXPreferencePage.StorePassword"), //$NON-NLS-1$
                parent));

        this.addField(new BooleanFieldEditor(
                MXAdapter.PREF_UPDATE_FILE_CONTENT,
                Messages.getString("MXPreferencePage.UpdateByFileContent"), //$NON-NLS-1$
                parent));
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
        @Override
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
