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

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 */
public class Decorator implements ILightweightLabelDecorator
{

    public void decorate(final Object _obj,
                         final IDecoration _idecoration)
    {
        final IFile file = (IFile) _obj;
        final String name = file.getName();

        for (final Map.Entry<String,Map<String,ImageDescriptor>> suffixEntry : Activator.IMAGEMAP.entrySet())  {
            if (name.endsWith(suffixEntry.getKey()))  {
                for (final Map.Entry<String, ImageDescriptor> entry : suffixEntry.getValue().entrySet())  {
                    if (name.startsWith(entry.getKey()))  {
                        _idecoration.addOverlay(entry.getValue(), IDecoration.TOP_LEFT);
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     * Method stub to implement interface {@link ILightweightLabelDecorator}.
     */
    public void addListener(final ILabelProviderListener _ilabelproviderlistener)
    {
    }

    /**
     * Method stub to implement interface {@link ILightweightLabelDecorator}.
     */
    public void dispose()
    {
    }

    /**
     * Method stub to implement interface {@link ILightweightLabelDecorator}.
     *
     * @return always <i>false</i>
     */
    public boolean isLabelProperty(final Object _obj,
                                   final String _s)
    {
        return false;
    }

    /**
     * Method stub to implement interface {@link ILightweightLabelDecorator}.
     */
    public void removeListener(final ILabelProviderListener _ilabelproviderlistener)
    {
    }
}
