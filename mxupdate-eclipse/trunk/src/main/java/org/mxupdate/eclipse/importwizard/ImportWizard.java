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

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.mxupdate.eclipse.Activator;
import org.mxupdate.eclipse.Messages;
import org.mxupdate.eclipse.adapter.IExportItem;
import org.mxupdate.eclipse.adapter.ISearchItem;

/**
 * Import wizard for configuration items directly from MX.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class ImportWizard
    extends Wizard
    implements IImportWizard
{
    /**
     * Step 1 page of the import wizard.
     */
    private Step1TypeNamePage step1;

    /**
     * Step 2 page of the import wizard.
     */
    private Step2ConfigurationItemsPage step2;

    /**
     * Performs the finish of the import for configuration items from MX. The
     * configuration items are exported from MX and new created in Eclipse
     * workspace.
     *
     * @return always <i>true</i>
     */
    @Override()
    public boolean performFinish()
    {
        final Set<ISearchItem> selectedItems = this.step2.getSelectedItems();
        final IPath targetPath = this.step2.getTargetPath();

        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(final IProgressMonitor _monitor)
            {
                for (final ISearchItem selected : selectedItems)  {

                    Activator.getDefault().getConsole().logInfo(Messages.getString("ImportWizard.WizardPerformFinishLog", //$NON-NLS-1$
                                                                                   selected.getFileName()));

                    // export file from MX
                    final IExportItem exportItem = Activator.getDefault().getAdapter().export(selected.getTypeDef(), selected.getName());
                    // and create file in Eclipse
                    if (exportItem != null)  {
                        final IPath newPath = targetPath.append(exportItem.getFilePath()).append(exportItem.getFileName());
                        final IFile newFileHandle = ResourcesPlugin.getWorkspace().getRoot().getFile(newPath);

                        final CreateFileOperation op = new CreateFileOperation(
                                newFileHandle,
                                null,
                                new ByteArrayInputStream(exportItem.getContent().getBytes()),
                                Messages.getString("ImportWizard.WizardPerformFinishTitle")); //$NON-NLS-1$
                        try
                        {
                            op.execute(_monitor, WorkspaceUndoUtil.getUIInfoAdapter(ImportWizard.this.getContainer().getShell()));
                        }
                        catch(final ExecutionException ex)
                        {
                            Activator.getDefault().getConsole().logError(Messages.getString("ImportWizard.WizardPerformFinishException"), ex); //$NON-NLS-1$
                        }
                    }
                }
            }
        };
        try
        {
            this.getContainer().run(true, true, op);
        }
        catch(final InterruptedException ex)
        {
            Activator.getDefault().getConsole().logError(Messages.getString("ImportWizard.WizardPerformFinishInterrupted")); //$NON-NLS-1$
        }
        catch(final InvocationTargetException ex)
        {
            Activator.getDefault().getConsole().logError(Messages.getString("ImportWizard.WizardPerformFinishException"), ex); //$NON-NLS-1$
        }

        return true;
    }

    /**
     * Initializes the two different steps for this wizard.
     *
     * @param _workbench    related workbench
     * @param _selection    structured selection
     * @see #step1
     * @see #step2
     */
    public void init(final IWorkbench _workbench,
                     final IStructuredSelection _selection)
    {
        this.setWindowTitle(Messages.getString("ImportWizard.WizardHeader")); //$NON-NLS-1$
        this.setNeedsProgressMonitor(true);
        this.step1 = new Step1TypeNamePage();
        this.step2 = new Step2ConfigurationItemsPage(this.step1, _selection);
    }

    /**
     * Appends {@link #step1 first} and {@link #step2 second step} page to this
     * wizard.
     *
     * @see #step1
     * @see #step2
     */
    @Override()
    public void addPages() {
        super.addPages();
        this.addPage(this.step1);
        this.addPage(this.step2);
    }
}
