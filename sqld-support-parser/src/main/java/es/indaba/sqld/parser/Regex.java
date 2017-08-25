/*
 * Copyright (c) 2002-2013, Hirondelle Systems. 
 * Copyright (c) 2017, Indaba Consultores 
 * All rights reserved.
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

/*
 * (UNPUBLISHED) Simple collection of commonly used regular expressions, as <tt>String</tt>s.
 * 
 * <P>
 * Regular expressions are both cryptic and error-prone. Reuse of the regular expressions in this class should increase
 * legibility in the caller, and reduce testing time.
 * 
 * <P>
 * (These are presented here as <tt>String</tt>s, and not as <tt>Pattern</tt>s, to aid in constructing complex
 * expressions out of simpler ones.)
 * 
 * <P>
 * Some items follow the style of <em>Mastering Regular Expressions</em>, by Jeffrey Friedl (0-596-00289-0).
 * 
 * <P>
 * Grouping represents a problem for this class, since the caller may or may not desire a particular element to be a
 * capturing group.
 */
public final class Regex {

    /*
     * Not a well-formed regex, but a symbolic name for alternation, intended simply to improve legibility of regexes.
     */
    public static final String OR = "|";

    /*
     * Whitespace.
     */
    public static final String WS = "\\s*";
    public static final String DOT = "\\.";
    public static final String ANY_CHARS = ".*";
    public static final String START_TAG = "<";
    public static final String END_TAG = WS + ">";
    public static final String ALL_BUT_END_OF_TAG = "[^>]*";
    public static final String ALL_BUT_START_OF_TAG = "[^<]*";
    public static final String QUOTE = "(?:'|\")";
    public static final String NO_SPECIAL_HTML_CHAR = "[^<>'\"]";

    /* Group 1 returns the attribute value. */
    public static final String QUOTED_ATTR = QUOTE + "((?:" + NO_SPECIAL_HTML_CHAR + ")*)" + QUOTE;

    /*  Group 1 returns the trimmed text. */
    public static final String TRIMMED_TEXT = "(?:\\s)*((?:\\S(?:.)*\\S)|(?:\\S))(?:\\s)*";

    // Might be used for unquoted attributes:
    // public static final String fNO_SPECIAL_HTML_CHARS_OR_SPACES =
    // "[^<>'\"\\s]";

    public static final int ENTIRE_MATCH = 0;
    public static final int FIRST_GROUP = 1;
    public static final int SECOND_GROUP = 2;
    public static final int THIRD_GROUP = 3;
    public static final int FOURTH_GROUP = 4;

    // NEW items added after web4j 1.3.0 :

    public static final String SINGLE_QUOTED_ATTR = "'[^']*'";
    public static final String DOUBLE_QUOTED_ATTR = "\"[^\"]*\"";
    public static final String UNQUOTED_ATTR = "[-.:\\w]+";

    /*
     * Group 1 is the attribute value, <em>with</em> quotes.
     * <P>
     * Use {@link Util#removeQuotes(String)} to remove any quotes, if needed.
     * <P>
     * The content of an HTML tag attribute is specified at http://www.w3.org/TR/html4/intro/sgmltut.html#attributes
     */
    public static final String ATTR_VALUE =
            "(" + SINGLE_QUOTED_ATTR + OR + DOUBLE_QUOTED_ATTR + OR + UNQUOTED_ATTR + ")";

    public static final String ATTR_NAME = "[a-zA-Z]+";
    public static final String TAG_NAME = "[a-zA-Z]+";
    public static final String ATTR = "\\s+" + ATTR_NAME + WS + "=" + WS + ATTR_VALUE;
    public static final String FIRST_TAG = "<" + TAG_NAME + "(?:" + ATTR + ")*" + WS + ">";
    public static final String SECOND_TAG = "</" + TAG_NAME + ">";
    public static final String TAG_BODY = "(.*?)";
    public static final String ENTIRE_TAG = FIRST_TAG + TAG_BODY + SECOND_TAG;

    public static final String BLANK_LINE = "^\\s*$";

    /*
     * Finds positions where a comma should be placed in a number, in the style <tt>1,000,000</tt>. Intended for
     * integers, but can also handle up to 3 decimal places. For example, <tt>10000.001</tt> gives <tt>10,000.001</tt> .
     */
    public static final String COMMA_INSERTION = "(?<=\\d)(?=(\\d\\d\\d)+(?!\\d))";

    /*
     * Either integer or floating point number. Has the following properties
     * <ul>
     * <li>digits with possible decimal point
     * <li>possible leading plus or minus sign
     * <li>no grouping delimiters (such as a comma)
     * <li>no leading or trailing whitespace
     * </ul>
     * <P>
     * Example matches: 1, 100, 2.3, -2.3, +2.3, -272.13, -.0, 2.
     * <P>
     * Example mismatches: '1,000', '123 ', ' 123'.
     */
    public static final String NUMBER =
            "(?:-|\\+)?" + "(" + "[0-9]+" + "(" + DOT + "[0-9]*)?" + OR + DOT + "[0-9]+" + ")";

    /*
     * Similar to {@link #NUMBER}, except the number of decimals, if present, is always 2.
     * 
     * <P>
     * Example matches : <tt>1000, 100.25, .25, 0.25, -.13, -0.13</tt>
     * <P>
     * Example mismatches : <tt>1,000.00, 100., 100.0, 100.123, -56.000, .123</tt>
     */
    public static final String DOLLARS =
            "(?:-|\\+)?" + "(" + "[0-9]+" + "(" + Regex.DOT + "[0-9]{2})?" + Regex.OR + Regex.DOT + "[0-9]{2}" + ")";

    /*
     * An amount in any currency.
     * 
     * <P>
     * There are two permitted decimal separators: <tt>'.'</tt> and <tt>','</tt> . The permitted number of decimals is
     * <tt>0,2,3</tt>.
     * 
     * <P>
     * Example matches : <tt>'1000', '100.25', '.253', '0.25', '-.13', '-0.00'</tt>
     * <P>
     * Example mismatches : <tt>'1,000.00', '100.', '100.0', '100.1234', ',1', ',1234'</tt>
     * 
     * <P>
     * Note as well that <tt>'1,000'</tt> matches as well! A grouping separator in one <tt>Locale</tt> is a decimal
     * separator in others.
     * 
     * <P>
     * Any <tt>String</tt> that matches this pattern will be accepted by
     * {@link java.math.BigDecimal#BigDecimal(String)}.
     */
    public static final String MONEY =
            "(?:-|\\+)?" + "(" + "[0-9]+((?:\\.|,)[0-9]{2,3})?" + Regex.OR + "(?:\\.|,)[0-9]{2,3}" + ")";

    /*
     * An arbitrary number of digits. Has the following properties
     * <ul>
     * <li>value greater than or equal to <tt>0</tt>
     * <li>possible leading zeros, as in <tt>0100</tt>, or <tt>0002</tt>
     * <li>no decimal point
     * <li>no leading plus or minus sign
     * <li>no leading or trailing whitespace
     * </ul>
     * 
     * <P>
     * <em>Design Note:</em><br>
     * Allowing leading zeros is not a problem for creating <tt>Integer</tt> objects, since the <tt>Integer(String)</tt>
     * constructor allows them.
     * 
     * <P>
     * Example matches: 0, 1, 2, 9, 10, 99, 789, 010, 0018.<br>
     * Example mismatches: -1, +1, 2.0, ' 0', '2 '.
     */
    public static final String DIGITS = "(\\d)+";

    /*
     * Return a regular expression corresponding to <tt>DIGITS</tt>, but having number of digits in range
     * <tt>1..aMaxNumDigits</tt>.
     * 
     * @param aMaxNumDigits must be <tt>1</tt> or more.
     */
    public static String forNDigits(int aMaxNumDigits) {
        Args.checkForRange(aMaxNumDigits, 1, Integer.MAX_VALUE);
        return "(\\d){1," + aMaxNumDigits + "}";
    }

    /*
     * Email address.
     * 
     * <P>
     * The {@link javax.mail.internet.InternetAddress} class permits validation of an email address. See also
     * {@link hirondelle.web4j.util.WebUtil#isValidEmailAddress(String)}. Thus, this regex <em>should be used only when
     * those classes are not available</em>.
     */
    public static final String EMAIL_ADDR = "\\w[-.\\w]*@" + "[a-z0-9]+(\\.[a-z0-9]+)*" + DOT
            + "(com|org|net|edu|gov|int|mil|biz|info|name|museum|coop|aero|[a-z][a-z])";

    /*
     * Matches numbers in the range 0-255.
     * <P>
     * Example: 1, 001, 010, 199, 255.
     */
    private static final String IP_ADDR_ITEM = "(?:[01]?\\d\\d?" + OR + "2[0-4]\\d" + OR + "25[0-5])";

    /*
     * IP addresses.
     * <P>
     * Example match: 1.01.001.255
     */
    public static final String IP_ADDR =
            "(?<![\\w.])" + IP_ADDR_ITEM + DOT + IP_ADDR_ITEM + DOT + IP_ADDR_ITEM + DOT + IP_ADDR_ITEM + "(?![\\w.])";

    /*
     * A positional regex which returns the position where lower case text is immediately followed by upper case text.
     * 
     * <P>
     * Intended for manipulation of text in camel hump style, which looks like this : BlahBlahBlah, LoginName,
     * EmailAddress.
     * 
     * <P>
     * Example:<br>
     * To change 'LoginName' into the more user-friendly 'Login Name' (with an added space), replace the matches
     * returned by this regex with the replacement string <tt>' $1'</tt>.
     */
    public static final String CAMEL_HUMP_TEXT = "(?<=[a-z0-9])([A-Z])";

    /*
     * Simple identifier. One or more letters/underscores, with possible trailing digits. Matching examples include :
     * <ul>
     * <li><tt>blah</tt>
     * <li><tt>blah42</tt>
     * <li><tt>blah_42</tt>
     * <li><tt>BlahBlah</tt>
     * <li><tt>BLAH_BLAH</tt>
     * </ul>
     */
    public static final String SIMPLE_IDENTIFIER = "([a-zA-Z_]+(?:\\d)*)";

    /*
     * Scoped identifier.
     * 
     * <P>
     * Either two {@link #SIMPLE_IDENTIFIER}s separated by a period, or a single {@link #SIMPLE_IDENTIFIER}. The item
     * before the period represents an <em>optional</em> scoping qualifier. (This style is used by SQL statement
     * identifiers, where the scoping qualifier represents the target database.)
     */
    // public static final String SIMPLE_SCOPED_IDENTIFIER = "(?:[a-zA-Z_]+(?:\\d)*\\.)?(?:[a-zA-Z_]+(?:\\d)*)";
    public static final String SIMPLE_SCOPED_IDENTIFIER = "(?:[a-zA-Z_]+[a-zA-Z_0-9]*\\.)?(?:[a-zA-Z_]+[a-zA-Z_0-9]*)";

    /*
     * A link or anchor tag.
     * 
     * <P>
     * Here, <tt>HREF</tt> must be the first attribute to appear in the tag. The following groups are defined :
     * <ul>
     * <li>group 1 - value of the HREF attr
     * <li>group 2 - all text after the HREF attr, but still inside the tag - the "remainder" attributes
     * <li>group 3 - the body of the A tag
     * </ul>
     */
    public static final String LINK = "<a href=" + Regex.QUOTED_ATTR + "(" + Regex.ALL_BUT_END_OF_TAG + ")"
            + Regex.END_TAG + Regex.TRIMMED_TEXT + "</a>";

    /*
     * Month in the Gregorian calendar: <tt>01..12</tt>.
     */
    public static final String MONTH = "(01|02|03|04|05|06|07|08|09|10|11|12)";

    /*
     * Day of the month in the Gregorian calendar: <tt>01..31</tt>.
     */
    public static final String DAY_OF_MONTH =
            "(01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)";

    /* Hours in the day <tt>00..23</tt>. */
    public static final String HOURS = "(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23)";

    /* Minutes in an hour <tt>00..59</tt>. */
    public static final String MINUTES = "((0|1|2|3|4|5)\\d)";

    /* Hours and minutes, in the form <tt>00:59</tt>. */
    public static final String HOURS_AND_MINUTES = HOURS + ":" + MINUTES;

    // PRIVATE //

    /* Prevents instantiation of this class. */
    private Regex() {
        // emtpy
    }
}
