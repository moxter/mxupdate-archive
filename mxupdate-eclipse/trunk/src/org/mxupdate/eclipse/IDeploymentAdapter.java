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

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Interface defining a common deployment adapter within eclipse needed to
 * reuse the UI implementation.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public interface IDeploymentAdapter
{
    /**
     * Connects to the database.
     *
     * @return <i>true</i> if connected; otherwise <i>false</i>
     */
    boolean connect();

    /**
     * Disconnects from the database.
     *
     * @return <i>true</i> if disconnected; otherwise <i>false</i>
     */
    boolean disconnect();

    /**
     * Updates given update <code>_files</code> in the database.
     *
     * @param _files    update files to update in the database
     */
    void update(final List<IFile> _files);

    /**
     * Extracts for given <code>_file</code> name the update code from the
     * database.
     *
     * @param _file update file for which the update code must be extracted
     * @return extracted update code
     * @throws Exception if the update code from the database could not be
     *                   extracted
     * @todo exceptions!
     */
    String extractCode(final IFile _file) throws Exception;

    /**
     * Executes given <code>_command</code> within the console.
     *
     * @param _command  command to execute
     * @return value from executed command
     * @todo exceptions!
     */
    String execute(final CharSequence _command) throws Exception;
}
