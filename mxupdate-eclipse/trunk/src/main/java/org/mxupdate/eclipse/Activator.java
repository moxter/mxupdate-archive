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
     * Adapter implementing the interface to MX.
     */
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
