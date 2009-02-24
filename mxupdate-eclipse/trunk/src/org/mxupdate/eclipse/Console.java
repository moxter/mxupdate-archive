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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * MxUpdate specific eclipse plug-in console.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class Console
        extends MessageConsole
{
    /**
     * Supported log levels for the MxUpdate console.
     */
    public enum LogLevel
    {
        /** Debug log level with blue color. */
        DEBUG(new Color(null, 0, 0, 255)),
        /** Error log level with dark red color. */
        ERROR(new Color(null, 128, 0, 0)),
        /** Info log level with black color. */
        INFO(new Color(null, 0, 0, 0)),
        /** Trace log level with gray color. */
        TRACE(new Color(null, 192, 192, 192)),
        /** Warning log level with dark magenta color. */
        WARN(new Color(null, 255, 0, 255));

        /**
         * Defines the used color for the log level.
         */
        final Color color;

        /**
         * Creates a new instance of the log level.
         *
         * @param _color    color for the log level
         */
        private LogLevel(final Color _color)
        {
          this.color = _color;
        }
      }

    /**
     * Map of all console streams depending on the log level (and the different
     * colors for each console).
     *
     * @see LogLevel
     */
    private final Map<Console.LogLevel,MessageConsoleStream> streams
            = new HashMap<Console.LogLevel,MessageConsoleStream>();

    /**
     * Creates new MxUpdate console with all {@link #streams} depending on the
     * log levels {@link LogLevel}.
     */
    public Console()
    {
        super(Messages.getString("plugin.console.label"), null); //$NON-NLS-1$
        for (final Console.LogLevel logLevel : Console.LogLevel.values())  {
            final MessageConsoleStream stream = this.newMessageStream();
            stream.setActivateOnWrite(true);
            stream.setColor(logLevel.color);
            this.streams.put(logLevel, stream);
        }
    }

    /**
     * Prints a text and stack trace of related exception. The MxUpdate console
     * is always shown before the text is printed.
     *
     * @param _logLevel   log level (used to add to the output)
     * @param _text       text to print
     * @param _e          exception with stack trace (or null if no exception is
     *                    defined)
     */
    public void println(final Console.LogLevel _logLevel,
                        final String _text,
                        final Throwable _e)
    {
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(this);
        final MessageConsoleStream stream = this.streams.get(_logLevel);
        final StringBuilder text = new StringBuilder().append(_text);

        if (_e != null)  {
            final StringWriter sw = new StringWriter();
            _e.printStackTrace(new PrintWriter(sw));
            text.append('\n')
                .append(sw.toString());
        }
        for (final String line : text.toString().split("\n"))  {
            stream.print('[' + _logLevel.name() + "] ");
            stream.println(line);
        }
        try {
            stream.flush();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace(System.out);
        }
    }
}
