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

package org.mxupdate.test.data;

import java.util.Map;

import matrix.util.MatrixException;

import org.mxupdate.test.AbstractTest;
import org.mxupdate.test.ExportParser;

/**
 * Defines common information from business objects used to create, update and
 * check them.
 *
 * @param <DATA>    class which is derived from this class
 * @author The MxUpdate Team
 * @version $Id$
 */
public abstract class AbstractBusData<DATA extends AbstractBusData<?>>
    extends AbstractData<DATA>
{
    /**
     * Description of the business object.
     *
     * @see #setDescription(String)
     * @see #create()
     * @see #ciFile()
     * @see #checkExport(ExportParser)
     */
    private String description;

    /**
     * Constructor to initialize this data piece.
     *
     * @param _test                 related test case
     * @param _ci                   related configuration type
     * @param _name                 name of the business object
     * @param _revision             revision of the business object
     */
    protected AbstractBusData(final AbstractTest _test,
                              final AbstractTest.CI _ci,
                              final String _name,
                              final String _revision)
    {
        super(_test, _ci, _name + "________" + _revision);
    }

    /**
     * Defines the {@link #description} of this business object instance.
     *
     * @param _description  description of the business object
     * @return this business object instance
     * @see #description
     */
    @SuppressWarnings("unchecked")
    public DATA setDescription(final String _description)
    {
        this.description =  _description;
        return (DATA) this;
    }

    /**
     * Creates the related MX business object for this data piece.
     *
     * @return this instance
     * @throws MatrixException if create failed
     */
    @Override()
    @SuppressWarnings("unchecked")
    public DATA create()
        throws MatrixException
    {
        final String[] nameArr = this.getName().split("________");
        final StringBuilder cmd = new StringBuilder()
                .append("escape add bus \"").append(AbstractTest.convertMql(this.getCI().getBusType()))
                .append("\" \"").append(AbstractTest.convertMql(nameArr[0]))
                .append("\" \"").append(AbstractTest.convertMql((nameArr.length > 1) ? nameArr[1] : ""))
                .append("\" description \"").append(AbstractTest.convertMql((this.description != null) ? this.description : ""))
                .append("\" policy \"").append(AbstractTest.convertMql(this.getCI().getBusPolicy()))
                .append("\" vault \"").append(AbstractTest.convertMql(this.getCI().getBusVault()))
                .append('\"');
        for (final Map.Entry<String,String> value : this.getValues().entrySet())  {
            cmd.append(" \"").append(AbstractTest.convertMql(value.getKey()))
               .append("\" \"").append(AbstractTest.convertMql(value.getValue()))
               .append('\"');
        }
        this.getTest().mql(cmd);
        return (DATA) this;
    }

    /**
     * Returns the TCL update file of this business object data instance.
     *
     * @return TCL update file content
     */
    @Override()
    public String ciFile()
    {
        final StringBuilder cmd = new StringBuilder()
                .append("mql escape mod bus \"${OBJECTID}\" description \"")
                .append(AbstractTest.convertTcl((this.description != null) ? this.description : ""))
                .append('\"');
        for (final Map.Entry<String,String> value : this.getValues().entrySet())  {
            cmd.append(" \\\n    \"").append(AbstractTest.convertTcl(value.getKey()))
               .append("\" \"").append(AbstractTest.convertTcl(value.getValue()))
               .append('\"');
        }
        return cmd.toString();
    }

    /**
     * Checks the export of this data piece if all values are correct defined.
     *
     * @param _exportParser     parsed export
     * @throws MatrixException if check failed
     */
    @Override()
    public void checkExport(final ExportParser _exportParser)
        throws MatrixException
    {
        super.checkExport(_exportParser);
        // check for defined values
        this.checkSingleValue(_exportParser,
                              "description",
                              "description",
                              "\"" + AbstractTest.convertTcl((this.description != null) ? this.description : "") + "\"");
        for (final Map.Entry<String,String> entry : this.getValues().entrySet())  {
            this.checkSingleValue(_exportParser,
                                  entry.getKey(),
                                  "\"" + AbstractTest.convertTcl(entry.getKey()) + "\"",
                                  "\"" + AbstractTest.convertTcl(entry.getValue()) + "\"");
        }
    }
}
