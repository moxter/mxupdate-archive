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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Common preference page for the MxUpdate eclipse plug-in to define URL, name
 * and password for the MX context.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class ProjectPropertyPage
    extends PropertyPage
{
    /**
     * Project specific properties edit from this property page.
     */
    private ProjectProperties properties;

    /**
     * @param _workbench    workbench which must be initialized
     */
    public void init(final IWorkbench _workbench)
    {
    }

    /**
     *
     */
    @Override()
    protected Control createContents(final Composite _parent)
    {
        final GridLayout layout = (GridLayout) _parent.getLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = false;

        final Combo selection = new Combo(_parent, SWT.READ_ONLY);
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 2;
        selection.setLayoutData(gridData);
        selection.setItems(new String [] {"", "MxUpdate via URL", "MxUpdate via URL with Property File",
                                              "MxUpdate via SSH + MQL", "MxUpdate via SSH + MQL with Property File",
                                              "MxUpdate with Property File"});

        final Map<Integer,ProjectMode> mapIdx2Modes = new HashMap<Integer,ProjectMode>();
        mapIdx2Modes.put(0, ProjectMode.UNKNOWN);
        mapIdx2Modes.put(1, ProjectMode.MXUPDATE_VIA_URL);
        mapIdx2Modes.put(2, ProjectMode.MXUPDATE_VIA_URL_WITH_PROPERTY_FILE);
        mapIdx2Modes.put(3, ProjectMode.MXUPDATE_SSH_MQL);
        mapIdx2Modes.put(4, ProjectMode.MXUPDATE_SSH_MQL_WITH_PROPERTY_FILE);
        mapIdx2Modes.put(5, ProjectMode.MXUPDATE_WITH_PROPERTY_FILE);


        final Map<ProjectMode,Composite> mapMode2Comp = new HashMap<ProjectMode,Composite>();

        selection.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(final SelectionEvent _event)
            {
            }
            public void widgetSelected(final SelectionEvent _event)
            {
                final ProjectMode selMode = mapIdx2Modes.get(selection.getSelectionIndex());
                ProjectPropertyPage.this.properties.setMode(selMode);
                for (final ProjectMode mode : ProjectMode.values())  {
                    if (mapMode2Comp.containsKey(mode))  {
                        final Composite modeComposite = mapMode2Comp.get(mode);
                        final boolean visible = (selMode == mode);
                        modeComposite.setVisible(visible);
                        ((GridData) modeComposite.getLayoutData()).exclude = !visible;
                    }
                }
                _parent.layout();
            }
        });

try {
    this.properties = new ProjectProperties((IProject) this.getElement(), this);
    this.properties.load();
} catch (final CoreException e) {
// TODO
    e.printStackTrace();
} catch (final IOException e) {
 // TODO
    e.printStackTrace();
}

final ProjectMode selMode = this.properties.getMode();

for (final Map.Entry<Integer,ProjectMode> entry : mapIdx2Modes.entrySet())  {
    if (entry.getValue() == selMode)  {
        selection.select(entry.getKey());
    }
}

for (final ProjectMode mode : ProjectMode.values())  {
    final boolean visible = (selMode == mode);

    final Composite modeComposite = FieldUtil.createComposite(_parent, visible);
    mode.createContent(modeComposite, this.properties);
    mapMode2Comp.put(mode, modeComposite);
}

        this.properties.checkValuesValid();

        return _parent;
    }

    @Override
    public boolean performOk()
    {
        // store the value in the owner text field

        boolean ret = true;
        try {
            this.properties.store();
        } catch (final CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = false;
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }
}
