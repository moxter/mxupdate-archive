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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.mxupdate.eclipse.Activator;
import org.mxupdate.eclipse.util.CommunicationUtil;
import org.osgi.framework.Bundle;

/**
 * Connector to MX via an URL with the MX Jar library (could be via web
 * application server, rmi server or via loaded shared library).
 *
 * @author The MxUpdate Team
 * @version $Id$
 * @see URLConnectorServer
 */
public class URLConnector
    extends AbstractConnector
{
    /**
     * Classes for the server which must be copied so that the server process
     * works.
     *
     * @see #URLConnector(IProject, String, String, String, String, String, boolean)
     */
    private static final Set<Class<?>> SERVER_CLASSES = new HashSet<Class<?>>();
    static {
        URLConnector.SERVER_CLASSES.add(CommunicationUtil.class);
        URLConnector.SERVER_CLASSES.add(URLConnectorServer.class);
    }

    /**
     * Name of the Apache URL codec library which is used for the Base64
     * encoding / decoding.
     *
     * @see #URLConnector(IProject, String, String, String, String, String, boolean)
     */
    private static final String CODEC_LIB = "org.apache.commons.codec_1.3.0.jar";

    /**
     * External Java process where the connection to MX is established.
     */
    private final Process process;

    /**
     * Output instance.
     */
    private final Writer out;

    /**
     * Initializes the URL connector to MX database. First the required
     * {@link #SERVER_CLASSES Java classes}Êand {@link #CODEC_LIB codec library}
     * are copied to the <code>_project</code> temporary directory. Then the
     * Java {@link #process} {@link URLConnectorServer} is started which
     * handles the connection to the MX database.
     *
     * @param _project              related eclipse project for which the URL
     *                              connector is initialized
     * @param _javaPath             Java path
     * @param _mxJarPath            path of the MX Jar library
     * @param _url                  URL of the MX server
     * @param _user                 MX user
     * @param _passwd               MX password
     * @param _updateByFileContent  <i>true</i> if update is done with file
     *                              content
     * @throws IOException if URL connector could not be initialized
     */
    public URLConnector(final IProject _project,
                        final String _javaPath,
                        final String _mxJarPath,
                        final String _url,
                        final String _user,
                        final String _passwd,
                        final boolean _updateByFileContent)
        throws IOException
    {
        super(_updateByFileContent);

        // copy the required classes and JAR library to temp directory
        final IPath path = Activator.getDefault().getStateLocation().append(_project.getName());
        if (!path.toFile().exists())  {
            path.toFile().mkdirs();
        }

        final Bundle bundle = Activator.getDefault().getBundle();
        for (final Class<?> clazz : URLConnector.SERVER_CLASSES)  {
            final String clazzFileName = "/" + clazz.getName().replace('.', File.separatorChar) + ".class";

            final URL url;
            // must be checked to differ between development and productive
            if (bundle.getEntryPaths("/bin") != null)  {
                url = bundle.getEntry("/bin" + clazzFileName);
            } else  {
                url = bundle.getEntry(clazzFileName);
            }

            this.copy(url, new File(path.toFile(), "/bin" + clazzFileName));
        }

        this.copy(Activator.getDefault().getBundle().getEntry("/lib/" + URLConnector.CODEC_LIB),
                  new File(path.toFile(), "/lib/" + URLConnector.CODEC_LIB));

        // prepare class path
        final StringBuilder classPath = new StringBuilder()
                .append("bin").append(File.separatorChar).append('.')
                .append(File.pathSeparatorChar)
                .append("lib").append(File.separatorChar).append(URLConnector.CODEC_LIB)
                .append(File.pathSeparatorChar)
                .append(_mxJarPath);

        // start process
        final ProcessBuilder pb = new ProcessBuilder(
                _javaPath,
                "-classpath", classPath.toString(),
                URLConnectorServer.class.getName(),
                _url,
                _user,
                _passwd);
        pb.directory(path.toFile());
        this.process = pb.start();

        this.out = new OutputStreamWriter(this.process.getOutputStream());

//        Thread.sleep(5000);

//        final Reader in = new InputStreamReader(process.getInputStream());
//        final Reader err = new InputStreamReader(process.getErrorStream());
/*
final Map<String,String> submit = new HashMap<String,String>();
submit.put("method", "myTestMethod");

String encoded = CommunicationUtil.encode(submit);


        final Writer out = new OutputStreamWriter(process.getOutputStream());
out.write(String.valueOf(encoded.length()));
out.write(' ');
out.write(encoded);
out.write(' ');
out.flush();

final Map<String,?> bck = CommunicationUtil.readParams(process.getInputStream());

*/

/*        Integer exitCode = null;
boolean first = true;
        for (;;) {
            if (err.ready())  {
                System.err.print((char) err.read());
            } else if (in.ready())  {
                System.out.print((char) in.read());
            } else  {
if (first)  {
    first = false;
}
                try {
                    exitCode = process.exitValue();
                    break;
                } catch (final IllegalThreadStateException e)  {
                    try {
                        Thread.sleep(1000);
                    } catch (final InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        }
*/
        // (exitCode != null) && (exitCode == 0);

    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException              if the {@link #out}Êstream could not be
     *                                  written
     * @throws ClassNotFoundException   if returned value from the server could
     *                                  not be decoded
     * @throws InterruptedException     if the {@link #process} is interrupted
     */
    public String execute(final String _arg1,
                          final String _arg2,
                          final String _arg3)
        throws IOException, ClassNotFoundException, InterruptedException
    {
        final Map<Integer,String> args = new HashMap<Integer,String>();
        args.put(0, "dispatch");
        args.put(1, _arg1);
        args.put(2, _arg2);
        args.put(3, _arg3);
        final String encoded = CommunicationUtil.encode(args);

        this.out.write(String.valueOf(encoded.length()));
        this.out.write(' ');
        this.out.write(encoded);
        this.out.write(' ');
        this.out.flush();

        final Map<String,?> bck = CommunicationUtil.readParams(this.process.getInputStream());

        return (String) bck.get("ret");
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException if the {@link #out stream} to the MX server could
     *                     not be written
     * @throws InterruptedException if the thread sleep failed (for the loops
     *                     while waiting for the exit of the {@link #process})
     */
    public void disconnect()
        throws IOException, InterruptedException
    {
        final Map<Integer,String> args = new HashMap<Integer,String>();
        args.put(0, "exit");

        final String encoded = CommunicationUtil.encode(args);

        this.out.write(String.valueOf(encoded.length()));
        this.out.write(' ');
        this.out.write(encoded);
        this.out.write(' ');
        this.out.flush();

        // destroy process if "normal" exit does not work...
        IllegalThreadStateException ex = null;
        int count = 0;
        do {
            ex = null;
            try  {
                this.process.exitValue();
            } catch (final IllegalThreadStateException e)  {
                ex = e;
                Thread.sleep(1000);
                count++;
            }
        } while ((ex != null) && (count < 10));
        if (ex != null)  {
            this.process.destroy();
        }
    }

    /**
     * Copy the content of the <code>_from</code> URL to the target
     * <code>_to</code> file.
     *
     * @param _from     URL of the file which must be copied
     * @param _to       target file to which must be copied
     * @throws IOException if the copy failed
     */
    private void copy(final URL _from,
                      final File _to)
        throws IOException
    {
        _to.getParentFile().mkdirs();
        if (!_to.exists())  {
            _to.createNewFile();
        }

        final InputStream in = _from.openStream();
        try  {
            final OutputStream fileOut = new FileOutputStream(_to);
            try {
                IOUtils.copy(in, fileOut);
            } catch (final Throwable e)  {
                e.printStackTrace();
            } finally {
                fileOut.close();
            }
        } finally  {
            in.close();
        }
    }
}
