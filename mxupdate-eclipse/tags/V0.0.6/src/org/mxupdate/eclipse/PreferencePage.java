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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Common preference page for the MxUpdate eclipse plug-in to define URL, name
 * and password for the MX context.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class PreferencePage
        extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    /**
     * Initialize the layout of the preference (defined as grid) and defines
     * the preference store from the plug-in activator {@link Activator}.
     */
    public PreferencePage()
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
    }

    /**
     * @param _workbench    workbench which must be initialized
     * @see IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(final IWorkbench _workbench)
    {
    }
}
