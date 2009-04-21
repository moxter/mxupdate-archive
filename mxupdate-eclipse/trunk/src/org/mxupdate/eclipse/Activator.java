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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import matrix.util.Mime64;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mxupdate.eclipse.console.Console;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the MxUpdate plug-in life cycle.
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class Activator extends AbstractUIPlugin {

    /**
     * Map used to hold the images for MxUpdate files. The first key is the
     * file extension, the second key the file prefix.
     */
    public static final Map<String,Map<String,ImageDescriptor>> IMAGEMAP
            = new HashMap<String,Map<String,ImageDescriptor>>();

    /**
     * ID of the MxUpdate plug-in.
     */
    public static final String PLUGIN_ID = "MxUpdateEclipsePlugIn"; //$NON-NLS-1$

    /**
     * Shared instance of the MxUpdate plug-in.
     */
    private static Activator plugIn;

    /**
     * MxUpdate plug-in console.
     *
     * @see #showConsole()
     */
    private Console console = null;

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
    private boolean connected = false;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
    @Override
    public void start(final BundleContext _context)
            throws Exception
    {
try  {
        super.start(_context);
        Activator.plugIn = this;

        this.console = new Console();
        this.console.activate();
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{this.console});

final Properties properties = new Properties();
final String propStr = this.getPluginPreferences().getString("pluginProperties"); //$NON-NLS-1$
if (propStr != null)  {
    final InputStream is = new ByteArrayInputStream(propStr.getBytes());
    properties.load(is);
}

// extract all admin type names
final Set<String> admins = new HashSet<String>();
for (final Object keyObj : properties.keySet())  {
    final String key = keyObj.toString().replaceAll("\\..*", ""); //$NON-NLS-1$ //$NON-NLS-2$
    admins.add(key);
}

// prepare image cache
for (final String admin : admins)  {
    final String prefix = properties.getProperty(admin + ".FilePrefix"); //$NON-NLS-1$
    final String suffix = properties.getProperty(admin + ".FileSuffix"); //$NON-NLS-1$
    final String iconStr = properties.getProperty(admin + ".Icon"); //$NON-NLS-1$

    byte[] bin = null;
    try {
        bin = Mime64.decode(iconStr);
    } catch (final IOException e) {
        e.printStackTrace();
    }
    final InputStream in = new ByteArrayInputStream(bin);

    final ImageData ret = new ImageData(in);


    Map<String,ImageDescriptor> mapPrefix = Activator.IMAGEMAP.get(suffix);
    if (mapPrefix == null)  {
        mapPrefix = new HashMap<String,ImageDescriptor>();
        Activator.IMAGEMAP.put(suffix, mapPrefix);
    }
    mapPrefix.put(prefix, ImageDescriptor.createFromImageData(ret));
}
} catch (final Throwable e)  {
    e.printStackTrace(System.out);
    this.console.logError("ERROR", e); //$NON-NLS-1$
}


    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext _context)
            throws Exception
    {
        ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[]{ this.console });
        this.console = null;
        Activator.plugIn = null;
        super.stop(_context);
    }

    /**
     * Returns the default shared instance of the MxUpdate eclipse plug-in.
     *
     * @return shared instance
     * @see #plugIn
     */
    public static Activator getDefault()
    {
        return Activator.plugIn;
    }

    /**
     * Returns the console for the plug-in. The method is the getter method for
     * instance variable {@link #console}.
     *
     * @return console of the plug-in
     * @see #console
     */
    public Console getConsole()
    {
        return this.console;
    }

    /**
     * Shows the MxUpdate plug-in console.
     *
     * @see #console    MxUpdate plug-in console
     */
    public void showConsole()
    {
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(this.console);
    }

    /**
     *
     * @return <i>true</i> if already connected or connect to MX database was
     *         successfully; otherwise <i>false</i> is returned
     * @see #connected
     * @see #mxContext
     */
    public boolean connect()
    {
        boolean connect = false;
        if (this.connected)  {
            connect = true;
            this.console.logInfo(Messages.getString("Activator.AlreadyConnected")); //$NON-NLS-1$
        } else  {
            final String host =  this.getPluginPreferences().getString("url"); //$NON-NLS-1$
            final String user =  this.getPluginPreferences().getString("name"); //$NON-NLS-1$
            final String passwd =  this.getPluginPreferences().getString("password"); //$NON-NLS-1$

            try {
                this.mxContext = new Context(host);
                this.mxContext.resetContext(user, passwd, null);
                this.mxContext.connect();
                this.connected = this.mxContext.isConnected();
                connect = true;
                this.console.logInfo(Messages.getString("Activator.ConnectedTo", host)); //$NON-NLS-1$

                // read properties
                final String newProps = this.execMql("exec prog org.mxupdate.plugin.GetProperties"); //$NON-NLS-1$
                final String curProps = this.getPluginPreferences().getString("pluginProperties"); //$NON-NLS-1$
                if (!newProps.equals(curProps))  {
                    this.getPluginPreferences().setValue("pluginProperties", newProps); //$NON-NLS-1$
                    this.console.logInfo(Messages.getString("Activator.PluginPropertiesChanged")); //$NON-NLS-1$
                }

            } catch (final MatrixException e) {
                this.console.logError(Messages.getString("Activator.ConnectFailed"), e); //$NON-NLS-1$
            }
        }
        return connect;
    }

    /**
    *
    * @return <i>true</i> if already disconnected or disconnect from MX
    *         database was successfully; otherwise <i>false</i> is returned
    * @see #connected
    * @see #mxContext
    */
    public boolean disconnect()
    {
        boolean disconnect = false;
        if (!this.connected)  {
            this.console.logInfo(Messages.getString("Activator.AlreadyDisconnected")); //$NON-NLS-1$
            disconnect = true;
        } else  {
            try {
                this.mxContext.disconnect();
                this.mxContext = null;
                this.connected = false;
                disconnect = true;
                this.console.logInfo(Messages.getString("Activator.Disconnected")); //$NON-NLS-1$
            } catch (final MatrixException e) {
                this.console.logError(Messages.getString("Activator.DisconnectFailed"), e); //$NON-NLS-1$
            }
        }
        return disconnect;
    }

    /**
     *
     * @param _file     MxUpdate file which must be updated
     * @see #execMql(CharSequence)
     */
    public void update(final String _file)
    {
        try {
            final String ret = this.execMql(
                    "exec prog org.mxupdate.plugin.Update '" + _file + "';" //$NON-NLS-1$ //$NON-NLS-2$
            );

            this.console.logInfo(ret);
        } catch (final MatrixException e)  {
            this.console.logError(Messages.getString("Activator.UpdateFailed", _file), e); //$NON-NLS-1$
        }
    }

    /**
     * Executes given MQL command and returns the trimmed result of the MQL
     * execution. The MX context {@link #mxContext} is connected to the data
     * base if not already done.
     *
     * @param _cmd      MQL command to execute
     * @return trimmed result of the MQL execution
     * @throws MatrixException if MQL execution fails
     * @see #mxContext
     * @see #connect()
     */
    public String execMql(final CharSequence _cmd)
            throws MatrixException
    {
        if (!this.connected)  {
            this.connect();
        }

        final MQLCommand mql = new MQLCommand();
        mql.executeCommand(this.mxContext, _cmd.toString());
        if ((mql.getError() != null) && !"".equals(mql.getError()))  { //$NON-NLS-1$
            throw new MatrixException(mql.getError() + "\nMQL command was:\n" + _cmd);
        }
        return mql.getResult().trim();
    }
}
