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

package org.mxupdate.eclipse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mxupdate.eclipse.adapter.IDeploymentAdapter;
import org.mxupdate.eclipse.console.Console;
import org.mxupdate.eclipse.mxadapter.MXAdapter;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the MxUpdate plug-in life cycle.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class Activator
    extends AbstractUIPlugin
{

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

    private IDeploymentAdapter adapter;


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
final String propStr = this.getPreferenceStore().getString("pluginProperties"); //$NON-NLS-1$
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

    final byte[] bin = Base64.decodeBase64(iconStr.getBytes());
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
this.adapter = new MXAdapter(this.getPreferenceStore(), this.console);

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

    public IDeploymentAdapter getAdapter()
    {
        return this.adapter;
    }
}
