/*
 * Copyright 2008 The MxUpdate Team
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

package net.sourceforge.mxupdate.update.datamodel;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import matrix.db.Context;
import matrix.util.MatrixException;

import net.sourceforge.mxupdate.update.AbstractAdminObject_mxJPO;
import net.sourceforge.mxupdate.update.util.InfoAnno_mxJPO;

import static net.sourceforge.mxupdate.update.util.StringUtil_mxJPO.convertTcl;

/**
*
* @author tmoxter
* @version $Id$
*/
@InfoAnno_mxJPO(adminType = "expression",
                title = "EXPRESSION",
                filePrefix = "EXPRESSION_",
                fileSuffix = ".tcl",
                filePath = "datamodel/expression",
                description = "expression")
public class Expression_mxJPO
        extends AbstractAdminObject_mxJPO
{
    /**
     * Defines the serialize version unique identifier.
     */
    private static final long serialVersionUID = -2903151847643967098L;

    /**
     * Hold the expression itself.
     */
    private String expression = null;

    /**
     * Parses all expression specific expression URLs.
     *
     * @param _url      URL to parse
     * @param _content  content of the URL to parse
     */
    @Override
    protected void parse(final String _url,
                         final String _content)
    {
        if (_url.startsWith("/expression"))  {
            // to be ignored and read from method prepare because
            // the expression export does not work correctly for XML tags

        } else  {
            super.parse(_url, _content);
        }
    }

    /**
     * The ranges are sorted.
     *
     * @param _context   context for this request
     */
    @Override
    protected void prepare(final Context _context)
            throws MatrixException
    {
        final String cmd = new StringBuilder()
                .append("print expression \"").append(convertTcl(getName()))
                .append("\" select value dump")
                .toString();
        this.expression = execMql(_context, cmd);
        super.prepare(_context);
    }

    /**
     * Writes specific information about the cached expression to the given
     * writer instance.
     *
     * @param _out      writer instance
     */
    @Override
    protected void writeObject(final Writer _out)
            throws IOException
    {
        _out.append(" \\\n    ").append(isHidden() ? "hidden" : "!hidden");
        _out.append(" \\\n    value \"");
        final String expr = convertTcl(this.expression);
        // bug-fix: expression with starting and ending ' (but without ')
        // must have a " as first and last character
        if (expr.matches("^'[^']*'$"))  {
            _out.append("\\\"").append(expr).append("\\\"");
        } else  {
            _out.append(expr);
        }
        _out.append('\"');
    }

    /**
     * The method overwrites the original method to append the MQL statements
     * in the <code>_preMQLCode</code> to reset this expression:
     * <ul>
     * <li>set to not hidden</li>
     * <li>reset description and value (expression itself)</li>
     * </ul>
     *
     * @param _context          context for this request
     * @param _preMQLCode       MQL statements which must be called before the
     *                          TCL code is executed
     * @param _postMQLCode      MQL statements which must be called after the
     *                          TCL code is executed
     * @param _tclCode          TCL code from the file used to update
     * @param _tclVariables     map of all TCL variables where the key is the
     *                          name and the value is value of the TCL variable
     *                          (the value is automatically converted to TCL
     *                          syntax!)
     */
    @Override
    protected void update(final Context _context,
                          final CharSequence _preMQLCode,
                          final CharSequence _postMQLCode,
                          final CharSequence _tclCode,
                          final Map<String,String> _tclVariables)
            throws Exception
    {
        final StringBuilder preMQLCode = new StringBuilder()
                .append("mod ").append(getInfoAnno().adminType())
                .append(" \"").append(getName()).append('\"')
                .append(" !hidden description \"\" value \"\";\n");

        // append already existing pre MQL code
        preMQLCode.append(_preMQLCode);

        super.update(_context, preMQLCode, _postMQLCode, _tclCode, _tclVariables);
    }
}