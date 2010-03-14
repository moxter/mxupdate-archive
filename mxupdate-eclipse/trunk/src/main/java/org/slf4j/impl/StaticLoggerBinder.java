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

package org.slf4j.impl;

import org.mxupdate.eclipse.logger.SLF4JOverMavenLog;
import org.mxupdate.eclipse.logger.SLF4JOverMavenLogFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class StaticLoggerBinder
    implements LoggerFactoryBinder
{
    /**
     * {@inheritDoc}
     */
    public static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     *
     */
    private static final String LOGGER_FACTORY_CLASSSTR = (SLF4JOverMavenLog.class).getName();

    /**
     *
     */
    private final ILoggerFactory loggerFactory = new SLF4JOverMavenLogFactory();

    /**
     * {@inheritDoc}
     */
    public ILoggerFactory getLoggerFactory()
    {
      return this.loggerFactory;
    }

    /**
     * {@inheritDoc}
     */
    public String getLoggerFactoryClassStr()
    {
      return StaticLoggerBinder.LOGGER_FACTORY_CLASSSTR;
    }
}
