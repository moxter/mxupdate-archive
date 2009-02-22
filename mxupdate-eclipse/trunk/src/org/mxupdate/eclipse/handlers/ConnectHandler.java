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

package org.mxupdate.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.mxupdate.eclipse.Activator;
import org.mxupdate.eclipse.Messages;

/**
 * Eclipse Handler called from the connect command used to connect to the MX.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class ConnectHandler
        extends AbstractHandler
{
    /**
     * Calls the connect to MX data base method.
     *
     * @param _event  execution event
     * @return always <code>null</code>
     * @see Activator#connect()
     */
    public Object execute(final ExecutionEvent _event)
    {
        final boolean connected = Activator.getDefault().connect();
        if (!connected)  {
            Activator.getDefault().logError(Messages.getString("ConnectHandler.ConnectFailed")); //$NON-NLS-1$
        }
        return null;
    }
}