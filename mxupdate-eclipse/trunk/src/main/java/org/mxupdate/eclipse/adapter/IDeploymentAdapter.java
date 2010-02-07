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

package org.mxupdate.eclipse.adapter;

import java.util.List;
import java.util.Set;

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
     * @param _compile  if <i>true</i> all program files are compiled; if
     *                  <i>false</i> no files are compiled
     */
    void update(final List<IFile> _files,
                final boolean _compile);

    /**
     * Exports defined <code>_item</code> with depending <code>_typeDef</code>.
     *
     * @param _typeDef  type definition
     * @param _item     MX name to export
     * @return list of exported files
     */
    IExportItem export(final String _typeDef,
                       final String _item);

    /**
     * Extracts for given <code>_file</code> name the update code from the
     * database.
     *
     * @param _file update file for which the update code must be extracted
     * @return extracted update code
     */
    IExportItem export(final IFile _file);

    /**
     * Executes given <code>_command</code> within the console.
     *
     * @param _command  command to execute
     * @return value from executed command
     * @todo exceptions!
     */
    String execute(final CharSequence _command) throws Exception;

    /**
     * Evaluates the type tree hierarchy and returns the root.
     *
     * @return root type tree (or <code>null</code> if it could not evaluated)
     */
    ITypeDefRoot getTypeDefRoot();

    /**
     * Searches for given type definitions <code>_typeDefList</code> which
     * matches <code>_match</code>.
     *
     * @param _typeDefList  set of searched type definitions
     * @param _match        match for the search
     * @return list of found objects
     */
    List<ISearchItem> search(final Set<String> _typeDefList,
                             final String _match);
}
