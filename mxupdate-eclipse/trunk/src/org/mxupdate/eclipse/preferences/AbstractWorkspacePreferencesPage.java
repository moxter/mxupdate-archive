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

package org.mxupdate.eclipse.preferences;

import java.lang.reflect.ParameterizedType;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mxupdate.eclipse.Activator;
import org.mxupdate.eclipse.Messages;

/**
 *
 * @param <T>
 * @author Tim Moxter
 * @version $Id$
 */
public class AbstractWorkspacePreferencesPage<T extends AbstractWorkspacePreference<?>>
        extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    public AbstractWorkspacePreferencesPage()
    {
        super(FieldEditorPreferencePage.GRID);
    }

    @Override
    protected void createFieldEditors()
    {
        final Composite parent = this.getFieldEditorParent();

        final Class<? extends AbstractWorkspacePreference<?>> clazz = this.getPreferenceClass();

        for (final AbstractWorkspacePreference<?> pref : AbstractWorkspacePreference.values(clazz))  {
            final String label = Messages.getString(new StringBuilder()
                    .append(clazz.getSimpleName()).append('.').append(pref.name())
                    .toString());
            if (pref.getDefaultClazz().equals(FontData.class))  {
                this.addField(new FontFieldEditor(pref.name(), label, parent));
            } else if (pref.getDefaultClazz().equals(RGB.class))  {
                this.addField(new ColorFieldEditor(pref.name(), label, parent));
            } else if (pref.getDefaultClazz().equals(Integer.class))  {
                this.addField(new IntegerFieldEditor(pref.name(), label, parent));
            }

        }

    }

    public void init(final IWorkbench iworkbench)
    {
        this.setPreferenceStore(Activator.getDefault().getPreferenceStore());
        this.setDescription(Messages.getString(new StringBuilder()
                .append(this.getPreferenceClass().getSimpleName()).append(".Description")
                .toString()));
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends AbstractWorkspacePreference<?>> getPreferenceClass()
    {
        // get defined generic super class
        final ParameterizedType t = (ParameterizedType) this.getClass().getGenericSuperclass();
        // get class of defined preference class
        final Class<? extends AbstractWorkspacePreference<?>> clazz
                = (Class<? extends AbstractWorkspacePreference<?>>) t.getActualTypeArguments()[0];

        return clazz;
    }
}
