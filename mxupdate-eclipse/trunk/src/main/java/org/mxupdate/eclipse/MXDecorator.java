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

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * MX Update Eclipse plug-in specific decorator which evaluates depending on
 * the file prefixes and suffixes the related images.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class MXDecorator
        implements ILightweightLabelDecorator
{

    /**
     * Depending on the file suffixes and prefixes the related image descriptor
     * is selected and shown as decorator image.
     *
     * @param _obj          object which must be checked for decoration
     * @param _decoration   decoration from Eclipse where the images are
     *                      &quot;overlayed&quot; on the top left
     */
    public void decorate(final Object _obj,
                         final IDecoration _decoration)
    {
        final IFile file = (IFile) _obj;
        final String name = file.getName();

        for (final Map.Entry<String,Map<String,ImageDescriptor>> suffixEntry : Activator.IMAGEMAP.entrySet())  {
            if (name.endsWith(suffixEntry.getKey()))  {
                for (final Map.Entry<String, ImageDescriptor> entry : suffixEntry.getValue().entrySet())  {
                    if (name.startsWith(entry.getKey()))  {
                        _decoration.addOverlay(entry.getValue(), IDecoration.TOP_LEFT);
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
