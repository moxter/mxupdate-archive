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

package org.mxupdate.eclipse.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.team.ui.synchronize.SaveableCompareEditorInput;
import org.mxupdate.eclipse.Activator;
import org.mxupdate.eclipse.Messages;

/**
 * The class is used as handler for commands to compare local update files
 * against current update code from the data base.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class CompareHandler
        extends AbstractFileHandler
{
    /**
     * Opens for each defined file a compare editor.
     *
     * @param _files    files to compare
     * @see CompareEditor
     */
    @Override
    protected void execute(final List<IFile> _files)
    {
        for (final IFile file : _files)  {

            CompareUI.openCompareEditor(new CompareEditor(file));
        }
    }

    /**
     * Compare editor between the local update file and the current update file
     * extracted from the data base.
     */
    class CompareEditor
            extends SaveableCompareEditorInput
    {
        /**
         * Local file which must be compared against the current object in the
         * data base.
         */
        private final IFile file;

        /**
         *
         * @param _file     local update file
         * @see #file
         */
        CompareEditor(final IFile _file)
        {
            super(new CompareConfiguration(), null);
            this.getCompareConfiguration().setLeftEditable(true);
            this.getCompareConfiguration().setLeftLabel(Messages.getString("CompareHandler.LocaleFile")); //$NON-NLS-1$
            this.getCompareConfiguration().setRightEditable(false);
            this.getCompareConfiguration().setRightLabel(Messages.getString("CompareHandler.DataBase")); //$NON-NLS-1$
            this.file = _file;
        }

        /**
         * Dummy method because the method is only needed that
         * {@link SaveableCompareEditorInput} is implemented.
         */
        @Override
        protected void fireInputChange()
        {
        }

        /**
         * Fetches for given local update file the related extracted code of
         * the update file from the data base and returns an input editor used
         * to compare.
         *
         * @param _progressMonitor  progress monitor
         * @return input editor with the local update file and the current
         *         update code from the data base
         * @throws InvocationTargetException of the update file from the data
         *                                   base could not be fetched or the
         *                                   code of the update file could not
         *                                   be converted to bytes
         * @todo get bytes in UTF 8 must NOT be hard coded!
         */
        @Override
        protected ICompareInput prepareCompareInput(final IProgressMonitor _progressMonitor)
                throws InvocationTargetException
        {
            _progressMonitor.beginTask(Messages.getString("CompareHandler.TaskReadFromDataBase"), 2); //$NON-NLS-1$

            // get current update code
            final String cur;
            try {
                cur = Activator.getDefault().getAdapter().extractCode(this.file);
            } catch (final Exception ex) {
                throw new InvocationTargetException(ex);
            }

            // convert
            _progressMonitor.setTaskName(Messages.getString("CompareHandler.TaskConvertText")); //$NON-NLS-1$
            final byte[] buf;
            try {
                buf = cur.getBytes("UTF8"); //$NON-NLS-1$
            } catch (final UnsupportedEncodingException ex) {
                throw new InvocationTargetException(ex);
            }

            // and create new difference node
            return new DiffNode(SaveableCompareEditorInput.createFileElement(this.file), new ByteBufferType(buf))  {
                @Override
                public String getName()
                {
                    return Messages.getString("CompareHandler.Title", //$NON-NLS-1$
                                              CompareHandler.CompareEditor.this.file.getName());
                }
            };
        }
    }

    /**
     * Class used to implement needed {@link ITypedElement} interface which
     * is needed for the compare editor. The class handles only the original
     * update file extract from the data base.
     */
    class ByteBufferType
            implements ITypedElement,IStreamContentAccessor
    {
        /**
         * Buffer of the extracted original update file.
         */
        private final byte[] buf;

        /**
         * Initialize the buffer represent the data from the extracted original
         * update file.
         *
         * @param _buf  new buffer
         */
        ByteBufferType(final byte[] _buf)
        {
            this.buf = _buf;
        }

        /**
         * The method returns always <code>null</code> because no image is
         * shown for each side.
         *
         * @return always <code>null</code>
         */
        public Image getImage()
        {
            return null;
        }

        /**
         * The method returns always <code>null</code> because the name itself
         * is not used to evaluate the title of the compare.
         *
         * @return always <code>null</code>
         */
        public String getName()
        {
            return null;
        }

        /**
         * The original code from the data base is always a text.
         *
         * @return always {@link ITypedElement#TEXT_TYPE}
         */
        public String getType()
        {
            return ITypedElement.TEXT_TYPE;
        }

        /**
         * Returns an input stream to the buffer {@link #buf}.
         *
         * @return stream to the buffer
         * @see #buf
         */
        public InputStream getContents()
        {
            return new ByteArrayInputStream(this.buf);
        }
    }
}
