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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Tim Moxter
 * @version $Id$
 */
public class PreferencePage
        extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    public PreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
        this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = this.getFieldEditorParent();

        final StringFieldEditor urlField = new StringFieldEditor(
                "url", //$NON-NLS-1$
                Messages.getString("PreferencePage.Host"), //$NON-NLS-1$
                parent);
        this.addField(urlField);

        final StringFieldEditor nameField = new StringFieldEditor(
                "name", //$NON-NLS-1$
                Messages.getString("PreferencePage.UserName"), //$NON-NLS-1$
                parent);
        this.addField(nameField);

        final StringFieldEditor passwdField = new StringFieldEditor(
                "password", //$NON-NLS-1$
                Messages.getString("PreferencePage.Password"), //$NON-NLS-1$
                parent);
        this.addField(passwdField);

        this.addField(new MyTextEditor(this.getFieldEditorParent()));
    }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
    public void init(final IWorkbench _workbench)
    {
    }


    class MyTextEditor extends StringFieldEditor
    {

        MyTextEditor(final Composite _parent)
        {
            super("pluginProperties", //$NON-NLS-1$
                  Messages.getString("PreferencePage.PluginProperties"), _parent); //$NON-NLS-1$
        }

        /**
         * Fills this field editor's basic controls into the given parent.
         * <p>
         * The string field implementation of this <code>FieldEditor</code>
         * framework method contributes the text field. Subclasses may override
         * but must call <code>super.doFillIntoGrid</code>.
         * </p>
         */
        @Override
        protected void doFillIntoGrid(final Composite _parent,
                                      final int _numColumns)
        {
            super.doFillIntoGrid(_parent, _numColumns);

            final Text textField = this.getTextControl();

            final GridData gd = new GridData();
            gd.horizontalSpan = _numColumns - 1;
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
            gd.verticalSpan = 15;
            gd.verticalAlignment = GridData.FILL;
            gd.grabExcessVerticalSpace = true;
            textField.setLayoutData(gd);
        }

        /**
         * Returns this field editor's text control.
         * <p>
         * The control is created if it does not yet exist
         * </p>
         *
         * @param _parent the parent
         * @return the text control
         */
        @Override
        public Text getTextControl(final Composite _parent)
        {
            Text textField = this.getTextControl();

            if (textField == null) {
                textField = new Text(_parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
                textField.setFont(_parent.getFont());
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(final KeyEvent _e) {
                        PreferencePage.MyTextEditor.this.clearErrorMessage();
                    }
                });
                textField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(final FocusEvent _e) {
                        PreferencePage.MyTextEditor.this.refreshValidState();
                    }
                    @Override
                    public void focusLost(final FocusEvent _e) {
                        PreferencePage.MyTextEditor.this.valueChanged();
                        PreferencePage.MyTextEditor.this.clearErrorMessage();
                    }
                });
                textField.addDisposeListener(new DisposeListener() {
                    public void widgetDisposed(final DisposeEvent _event) {
    //            textField = null;
                    }
                });
            }
            return textField;
        }
    }
}
