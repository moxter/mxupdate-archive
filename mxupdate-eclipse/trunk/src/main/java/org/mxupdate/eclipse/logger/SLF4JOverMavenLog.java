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

import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class SLF4JOverMavenLog
    extends MarkerIgnoringBase
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1200409589535113201L;

    /**
     * Start time where this class is initialized.
     */
    private static final long START_TIME = System.currentTimeMillis();

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * String text for the info level.
     */
    private static final String INFO_STR = "INFO";

    /**
     * String text for the warn level.
     */
    private static final String WARN_STR = "WARN";

    /**
     * String text for the error level.
     */
    private static final String ERROR_STR = "ERROR";

    @Override()
    public void error(final String _message)
    {
        this.log(SLF4JOverMavenLog.ERROR_STR, _message, null);
    }

    @Override()
    public void error(final String _message,
                      final Object _argument)
    {
        this.formatAndLog(SLF4JOverMavenLog.ERROR_STR, _message, _argument);
    }

    @Override
    public void error(final String _message,
                      final Object _argument1,
                      final Object _argument2)
    {
        this.formatAndLog(SLF4JOverMavenLog.ERROR_STR, _message, _argument1, _argument2);
    }

    @Override()
    public void error(final String _message,
                      final Object[] _arguments)
    {
        this.formatAndLog(SLF4JOverMavenLog.ERROR_STR, _message, _arguments);
    }

    @Override()
    public void error(final String _message,
                      final Throwable _throwable)
    {
        this.formatAndLog(SLF4JOverMavenLog.ERROR_STR, _message, _throwable);
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message  message to trace
     */
    @Override()
    public void warn(final String _message)
    {
        this.log(SLF4JOverMavenLog.WARN_STR, _message, null);
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to warn
     * @param _argument     argument
     */
    @Override()
    public void warn(final String _message,
                     final Object _argument)
    {
        this.formatAndLog(SLF4JOverMavenLog.WARN_STR, _message, _argument);
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to warn
     * @param _argument1    first argument
     * @param _argument2    second argument
     */
    @Override()
    public void warn(final String _message,
                     final Object _argument1,
                     final Object _argument2)
    {
        this.formatAndLog(SLF4JOverMavenLog.WARN_STR, _message, _argument1, _argument2);
    }

    @Override()
    public void warn(final String _message,
                     final Object[] _arguments)
    {
        this.formatAndLog(SLF4JOverMavenLog.WARN_STR, _message, _arguments);
    }

    @Override()
    public void warn(final String _message,
                     final Throwable _throwable)
    {
        this.formatAndLog(SLF4JOverMavenLog.ERROR_STR, _message, _throwable);
    }

    @Override()
    public void info(final String _message)
    {
        this.log(SLF4JOverMavenLog.INFO_STR, _message, null);
    }

    @Override()
    public void info(final String _message,
                     final Object _argument)
    {
        this.formatAndLog(SLF4JOverMavenLog.INFO_STR, _message, _argument);
    }

    @Override()
    public void info(final String _message,
                     final Object _argument1,
                     final Object _argument2)
    {
        this.formatAndLog(SLF4JOverMavenLog.INFO_STR, _message, _argument1, _argument2);
    }

    @Override()
    public void info(final String _message,
                     final Object[] _arguments)
    {
        this.formatAndLog(SLF4JOverMavenLog.INFO_STR, _message, _arguments);
    }

    @Override()
    public void info(final String _message,
                     final Throwable _throwable)
    {
        this.formatAndLog(SLF4JOverMavenLog.INFO_STR, _message, _throwable);
    }

    /**
     * @return always <i>true</i>
     */
    @Override()
    public boolean isErrorEnabled()
    {
        return true;
    }

    /**
     * @return always <i>true</i>
     */
    @Override()
    public boolean isWarnEnabled()
    {
        return true;
    }

    /**
     * @return always <i>true</i>
     */
    @Override()
    public boolean isInfoEnabled()
    {
        return true;
    }

    /**
     * @return always <i>false</i>
     */
    @Override()
    public boolean isDebugEnabled()
    {
        return false;
    }

    /**
     * @return always <i>false</i>
     */
    @Override()
    public boolean isTraceEnabled()
    {
        return false;
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message  message to debug
     */
    @Override()
    public void debug(final String _message)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to debug
     * @param _argument     argument
     */
    @Override()
    public void debug(final String _message,
                      final Object _argument)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to debug
     * @param _argument1    first argument
     * @param _argument2    second argument
     */
    @Override()
    public void debug(final String _message,
                      final Object _argument1,
                      final Object _argument2)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to debug
     * @param _arguments    arguments
     */
    @Override()
    public void debug(final String _message,
                      final Object[] _arguments)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to debug
     * @param _throwable    throwable instance
     */
    @Override()
    public void debug(final String _message,
                      final Throwable _throwable)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message  message to trace
     */
    @Override()
    public void trace(final String _message)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to trace
     * @param _argument     argument
     */
    @Override()
    public void trace(final String _message,
                      final Object _argument)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to trace
     * @param _argument1    first argument
     * @param _argument2    second argument
     */
    @Override()
    public void trace(final String _message,
                      final Object _argument1,
                      final Object _argument2)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to trace
     * @param _arguments    arguments
     */
    @Override()
    public void trace(final String _message,
                      final Object[] _arguments)
    {
    }

    /**
     * Dummy method to implement the interface.
     *
     * @param _message      message to debug
     * @param _throwable    throwable instance
     */
    @Override()
    public void trace(final String _message,
                      final Throwable _throwable)
    {
    }

    /**
     *
     * @param _level        level text for the logger
     * @param _format       text with format message
     * @param _args         arguments for the formatter
     */
    private void formatAndLog(final String _level,
                              final String _format,
                              final Object... _args)
    {
        this.log(_level, MessageFormatter.arrayFormat(_format, _args), null);
    }

    /**
     *
     * @param _level        level text for the logger
     * @param _message      message to log
     * @param _throwable    throwable instance
     */
    private void log(final String _level,
                     final String _message,
                     final Throwable _throwable)
    {
        final long millis = System.currentTimeMillis();
        final StringBuilder buf = new StringBuilder()
            .append(millis - SLF4JOverMavenLog.START_TIME)
            .append(" [").append(Thread.currentThread().getName()).append("] ")
            .append(_level)
            .append(" - ").append(_message)
            .append(SLF4JOverMavenLog.LINE_SEPARATOR);

        System.err.print(buf.toString());
        if(_throwable != null)  {
            _throwable.printStackTrace(System.err);
        }
        System.err.flush();
    }
}
