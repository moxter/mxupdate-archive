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

package org.mxupdate.update.datamodel;

import org.mxupdate.mapping.TypeDef_mxJPO;

/**
 * The class is used to evaluate information from date attributes within MX
 * used to export, delete and update a date attribute.
 *
 * @author The MxUpdate Team
 * @version $Id$
 */
public class AttributeDate_mxJPO
    extends AbstractAttribute_mxJPO
{
    /**
     * Constructor used to initialize the date attribute instance with
     * related type definition and attribute name.
     *
     * @param _typeDef  defines the related type definition
     * @param _mxName   MX name of the date attribute object
     */
    public AttributeDate_mxJPO(final TypeDef_mxJPO _typeDef,
                               final String _mxName)
    {
        super(_typeDef, _mxName, "date", "timestamp,");
    }
}
