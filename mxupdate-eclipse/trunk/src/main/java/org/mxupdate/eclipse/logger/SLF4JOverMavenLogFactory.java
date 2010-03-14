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

package org.mxupdate.eclipse.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * Factory for the MxUpdate Eclipse Plug-In SLF4J logger.
 *
 * @author The MxUdpate Team
 * @version $Id$
 */
public class SLF4JOverMavenLogFactory
    implements ILoggerFactory
{
    /**
     * Logger instance.
     */
    private final SLF4JOverMavenLog logInstance = new SLF4JOverMavenLog();

    /**
     * @param _name     name of the searched logger (not used)
     * @return the {@link #logInstance logger instance}
     * @see #logInstance
     */
    public Logger getLogger(final String _name)
    {
      return this.logInstance;
    }
}
