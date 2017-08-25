/*
 * Copyright (c) 2002-2013, Hirondelle Systems. Copyright (c) 2017, Indaba Consultores All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of
 * Hirondelle Systems nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY HIRONDELLE SYSTEMS AND CONTRIBUTORS ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL HIRONDELLE SYSTEMS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package es.indaba.sqld.parser;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Static convenience methods for common tasks, which eliminate code duplication.
 */
public final class Util {

    public static final String SINGLE_QUOTE = "'";
    
    /*
     * Return a {@link Logger} whose name follows a specific naming convention.
     * 
     * <P> The conventional logger names used by WEB4J are taken as <tt>aClass.getPackage().getName()</tt>.
     * 
     * <P> Logger names appearing in the <tt>logging.properties</tt> config file must match the names returned by this
     * method.
     * 
     * <P> If an application requires an alternate naming convention, then an alternate implementation can be easily
     * constructed. Alternate naming conventions might account for : <ul> <li>pre-pending the logger name with the name
     * of the application (this is useful where log handlers are shared between different applications) <li>adding
     * version information </ul>
     */
    public static Logger getLogger(Class<?> aClass) {
        return Logger.getLogger(aClass.getPackage().getName());
    }



    /*
     * Return true only if <tt>aText</tt> is non-null, and matches <tt>aPattern</tt>.
     * 
     * <P> Differs from {@link Pattern#matches} and {@link String#matches}, since the regex argument is a compiled
     * {@link Pattern}, not a <tt>String</tt>.
     */
    public static boolean matches(Pattern aPattern, String aText) {
        /*
         * Implementation Note: Patterns are thread-safe, while Matchers are not. Thus, a Pattern may be compiled by a
         * class once upon startup, then reused safely in a multi-threaded environment.
         */
        if (aText == null)
            return false;
        Matcher matcher = aPattern.matcher(aText);
        return matcher.matches();
    }

    // PRIVATE //
    /*
     * Call {@link String#valueOf(Object)} on <tt>aObject</tt>, and place the result in single quotes. <P> This method
     * is a bit unusual in that it can accept a <tt>null</tt> argument : if <tt>aObject</tt> is <tt>null</tt>, it will
     * return <tt>'null'</tt>.
     * 
     * <P> This method reduces errors from leading and trailing spaces, by placing single quotes around the returned
     * text. Such leading and trailing spaces are both easy to create and difficult to detect (a bad combination).
     * 
     * <P> Note that such quotation is likely needed only for <tt>String</tt> data, since trailing or leading spaces
     * will not occur for other types of data.
     */
    public static String quote(Object aObject) {
        return SINGLE_QUOTE + String.valueOf(aObject) + SINGLE_QUOTE;
    }

    /*
     * Return <tt>true</tt> only if <tt>aText</tt> is not null, and is not empty after trimming. (Trimming removes both
     * leading/trailing whitespace and ASCII control characters. See {@link String#trim()}.)
     * 
     * <P> For checking argument validity, {@link Args#checkForContent} should be used instead of this method.
     * 
     * @param aText possibly-null.
     */
    public static boolean textHasContent(String aText) {
        return (aText != null) && (aText.trim().length() > 0);
    }

    private Util() {
        // empty - prevents construction by the caller.
    }
}
