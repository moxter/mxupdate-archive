package net.sourceforge.mxupdate.update.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Tim Moxter
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TagName_mxJPO
{
    public String value();
}
