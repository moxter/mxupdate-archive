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

package org.mxupdate.eclipse.importwizard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.WizardResourceImportPage;
import org.mxupdate.eclipse.Activator;
import org.mxupdate.eclipse.Messages;
import org.mxupdate.eclipse.adapter.ISearchItem;

/**
 * Second step of the import wizard for configuration items. On the page first
 * a folder selection is shown where the imported configuration items will be
 * created. Then a table with all found configuration items are shown. The
 * configuration which are imported must be selected within this table.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Step2ConfigurationItemsPage
    extends WizardResourceImportPage
{
    /**
     * Link to first step of import wizard needed to get the search parameters.
     *
     * @see #setVisible(boolean)
     */
    private final Step1TypeNamePage step1;

    /**
     * Table with the found configuration items of the search.
     */
    private Table table;

    /**
     * Initializes step 2 page of the import wizard.
     *
     * @param _typeNamePage     reference to first step of the wizard
     * @param _selection        structured selection
     */
    public Step2ConfigurationItemsPage(final Step1TypeNamePage _typeNamePage,
                                       final IStructuredSelection _selection)
    {
        super("Step2", _selection);
        this.step1 = _typeNamePage;
        this.setTitle(Messages.getString("ImportWizard.Wizard.Step2.Title")); //$NON-NLS-1$
        this.setDescription(Messages.getString("ImportWizard.Wizard.Step2.Description")); //$NON-NLS-1$
    }

    /**
     * If this page is shown the search for configuration items is performed
     * depending on the defined values from the {@link #step1 first step} of
     * the wizard.
     *
     * @param _visible  must be <i>true</i> that the search is executed
     */
    @Override()
    public void setVisible(final boolean _visible)
    {
        if (_visible)  {
            final List<ISearchItem> items = Activator.getDefault().getAdapter().search(this.step1.getTypeDefs(), this.step1.getMatch());

            this.table.removeAll();
            for (final ISearchItem item : items)  {
                final TableItem tableItem = new TableItem(this.table, SWT.NONE);
                tableItem.setText(new String[]{item.getName(), item.getFileName(), item.getFilePath()});
                tableItem.setData(item);
            }
        }
        super.setVisible(_visible);
    }

    /**
     * Creates the control for the second step of the wizard. The original
     * method is overwritten so that the destination group is shown above the
     * source group.
     *
     * @param _parent   parent composite
     */
    @Override()
    public void createControl(final Composite _parent)
    {
        this.initializeDialogUnits(_parent);

        final Composite containerGroup = new Composite(_parent, 0);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(768));
        containerGroup.setFont(_parent.getFont());

        this.createDestinationGroup(containerGroup);

        this.createSourceGroup(containerGroup);

        this.restoreWidgetValues();
        this.setControl(containerGroup);
        this.updateWidgetEnablements();
    }

    /**
     * Appends the table where the user could select the configuration items
     * which are exported.
     *
     * @param _parent   parent composite where the table must be appended
     */
    @Override()
    protected void createSourceGroup(final Composite _parent)
    {
        // group for the table
        final Group group  = new Group(_parent, SWT.SHADOW_IN);
        final GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 2;
        group.setLayout(groupLayout);
        final GridData groupGridData = new GridData();
        groupGridData.verticalAlignment = GridData.FILL;
        groupGridData.grabExcessVerticalSpace = true;
        groupGridData.horizontalAlignment = GridData.FILL;
        groupGridData.grabExcessHorizontalSpace = true;
        group.setLayoutData(groupGridData);
        group.setText(Messages.getString("ImportWizard.Wizard.Step2.TableGroupTitle")); //$NON-NLS-1$

        // result table
        this.table = new Table(group, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        final GridData tableGridData = new GridData();
        tableGridData.verticalAlignment = GridData.FILL;
        tableGridData.grabExcessVerticalSpace = true;
        tableGridData.horizontalAlignment = GridData.FILL;
        tableGridData.horizontalSpan = 2;
        tableGridData.grabExcessHorizontalSpace = true;
        this.table.setLayoutData(tableGridData);
        this.table.setHeaderVisible(true);
        this.table.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(final SelectionEvent _event)
            {
            }

            public void widgetSelected(final SelectionEvent _event)
            {
                Step2ConfigurationItemsPage.this.updateWidgetEnablements();
            }
        });
        // table columns
        final TableColumn tc1 = new TableColumn(this.table, SWT.LEFT);
        final TableColumn tc2 = new TableColumn(this.table, SWT.LEFT);
        final TableColumn tc3 = new TableColumn(this.table, SWT.LEFT);
        tc1.setText(Messages.getString("ImportWizard.Wizard.Step2.TableHeaderName")); //$NON-NLS-1$
        tc2.setText(Messages.getString("ImportWizard.Wizard.Step2.TableHeaderFile")); //$NON-NLS-1$
        tc3.setText(Messages.getString("ImportWizard.Wizard.Step2.TableHeaderPath")); //$NON-NLS-1$
        tc1.setWidth(150);
        tc2.setWidth(200);
        tc3.setWidth(150);

        // select all button
        final Button buttonAll = new Button(group, SWT.PUSH);
        buttonAll.setText(Messages.getString("ImportWizard.Wizard.Step2.TableSelectAll")); //$NON-NLS-1$
        buttonAll.setFont(group.getFont());
        buttonAll.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(final SelectionEvent _event)
            {
            }
            public void widgetSelected(final SelectionEvent _event)
            {
                Step2ConfigurationItemsPage.this.table.selectAll();
                Step2ConfigurationItemsPage.this.updateWidgetEnablements();
            }
        });

        // deselect all button
        final Button buttonClear = new Button(group, SWT.PUSH);
        buttonClear.setText(Messages.getString("ImportWizard.Wizard.Step2.TableSelectClear")); //$NON-NLS-1$
        buttonClear.setFont(group.getFont());
        buttonClear.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(final SelectionEvent _event)
            {
            }
            public void widgetSelected(final SelectionEvent _event)
            {
                Step2ConfigurationItemsPage.this.table.deselectAll();
                Step2ConfigurationItemsPage.this.updateWidgetEnablements();
            }
        });
    }

    /**
     * Checks if at minimum one configuration item from {@link #table} is
     * selected. If not an error message is shown.
     *
     * @return <i>true</i> if at minimum on configuration item is selected;
     *         otherwise <i>false</i>
     */
    @Override()
    protected boolean validateSourceGroup()
    {
        final boolean ret;
        if ((this.table != null) && this.table.getSelectionCount() == 0)  {
            this.setErrorMessage(Messages.getString("ImportWizard.Wizard.Step2.TableError")); //$NON-NLS-1$
            ret = false;
        } else  {
            ret = true;
        }
        return ret;
    }

    /**
     * Dummy method needed to fulfill inheritance.
     *
     * @return always <code>null</code>
     */
    @Override()
    protected ITreeContentProvider getFileProvider()
    {
        return null;
    }

    /**
     * Dummy method needed to fulfill inheritance.
     *
     * @return always <code>null</code>
     */
    @Override()
    protected ITreeContentProvider getFolderProvider()
    {
        return null;
    }

    /**
     * Returns the set of select type definitions in the {@link #tree}.
     *
     * @return set of type definitions
     */
    public Set<ISearchItem> getSelectedItems()
    {
        final Set<ISearchItem> ret = new HashSet<ISearchItem>();
        for (final TableItem item : this.table.getSelection())  {
            ret.add((ISearchItem) item.getData());
        }
        return ret;
    }

    /**
     * Returns the target path where the configuration items must be imported.
     *
     * @return target path
     */
    public IPath getTargetPath()
    {
        return this.getContainerFullPath();
    }
}
