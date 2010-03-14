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

package org.mxupdate.eclipse.mxadapter.connectors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

import org.mxupdate.eclipse.util.CommunicationUtil;

/**
 * Server program which handles the connection to the MX database as proxy to
 * the MxUpdate Eclipse plug-in.
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @see URLConnector
 */
public class URLConnectorServer
{
    /**
     * Context to the MX database.
     *
     * @see #connected
     * @see #connect()
     * @see #disconnect()
     */
    private Context mxContext;

    /**
     * Is {@link #mxContext} connected to the MX database?
     *
     * @see #mxContext
     * @see #connect()
     * @see #disconnect()
     */
    private final boolean connected = false;

    /**
     * URL of the host for the MX connection.
     */
    private final String host;

    /**
     * Name of the MX user.
     */
    private final String user;

    /**
     * Password of the MX user.
     */
    private final String passwd;

    /**
     * Starts the server process for the {@link URLConnector}.
     *
     * @param _args     arguments, where
     *                  <ul>
     *                  <li>first argument defines the host name</li>
     *                  <li>second argument defines the user name</li>
     *                  <li>third argument defines the password</li>
     *                  </ul>
     * @throws Exception if execute failed
     * @see #WebServerServer(String, String, String)
     * @see #run()
     */
    public static void main(final String... _args)
        throws Exception
    {
        new URLConnectorServer(_args[0], _args[1], _args[2]).run();
    }

    /**
     * Initializes the {@link #host}, {@link #user} and {@link #passwd} needed
     * to open the {@link #mxContext context} to MX.
     *
     * @param _host     URL to the host address of the MX server
     * @param _user     MX user
     * @param _passwd   password of the MX user
     */
    public URLConnectorServer(final String _host,
                              final String _user,
                              final String _passwd)
    {
        this.host = _host;
        this.user = _user;
        this.passwd = _passwd;
    }

    /**
     * Runs the process for the execution of the dispatch processes to the MX
     * database.
     *
     * @throws IOException              if write into the streams or read from
     *                                  the streams failed
     * @throws ClassNotFoundException   if class is not found within encoding /
     *                                  decoding
     * @throws InterruptedException     if process is interrupted
     * @throws MatrixException          is execution failed
     */
    protected void run()
        throws IOException, ClassNotFoundException, InterruptedException, MatrixException
    {
        while (true)  {
            final Map<Integer,String> params = CommunicationUtil.<Integer,String>readParams(System.in);

            final String method = params.get(0);
            final String bck;
            if ("dispatch".equals(method))  {
                bck = this.executeEncoded(params.get(1), params.get(2), params.get(3));
            } else if ("exit".equals(method))  {
                break;
            } else  {
                bck = null;
            }

            final Map<String,String> ret = new HashMap<String,String>();
            ret.put("ret", bck);

            final String retStr = CommunicationUtil.encode(ret);
            System.out.print(retStr.length());
            System.out.print(' ');
            System.out.print(retStr);
            System.out.print(' ');
            System.out.flush();
        }

        this.mxContext.disconnect();
    }

    /**
     * Connects the the MX database.
     *
     * @throws MatrixException if connect failed
     * @see #host
     * @see #user
     * @see #passwd
     */
    protected void connect()
        throws MatrixException
    {
        this.mxContext = new Context(this.host);
        this.mxContext.resetContext(this.user, this.passwd, null);
        this.mxContext.connect();
    }

    /**
     * Calls given <code>_method</code> in of the MxUpdate eclipse plug-in
     * dispatcher. The MX context {@link #mxContext} is connected to the
     * database if not already done.
     *
     * @param _parameters   parameters
     * @param _method       method of the called <code>_jpo</code>
     * @param _arguments    list of all arguments for the <code>_jpo</code>
     *                      which are automatically encoded encoded
     * @return returned value from the called <code>_jpo</code>
     * @throws MatrixException  if the called <code>_jpo</code> throws an
     *                          exception
     * @see #mxContext
     * @see #connect()
     */
    protected String executeEncoded(final String _parameters,
                                    final String _method,
                                    final String _arguments)
        throws MatrixException
    {
        if (!this.connected)  {
            this.connect();
        }

        // prepare MQL statement with encoded parameters
        final StringBuilder cmd = new StringBuilder()
            .append("exec prog ").append("org.mxupdate.plugin.Dispatcher \"")
            .append(_parameters).append("\" \"")
            .append(_method).append("\" \"")
            .append(_arguments).append("\"");

        // execute MQL command
        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(this.mxContext, cmd.toString());
        if ((mql.getError() != null) && !"".equals(mql.getError()))  { //$NON-NLS-1$
            throw new MatrixException(mql.getError());
        }
        return mql.getResult();
    }
}
