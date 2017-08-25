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

/**
 * Collected constants of general utility.
 * 
 * <P>
 * All members of this class are immutable.
 * 
 * <P>
 * (This is an example of <a href='http://www.javapractices.com/Topic2.cjp'>class for constants</a>.)
 */
public final class Consts {

    /** Opposite of {@link #FAILS}. */
    public static final boolean PASSES = true;
    /** Opposite of {@link #PASSES}. */
    public static final boolean FAILS = false;

    /** Opposite of {@link #FAILURE}. */
    public static final boolean SUCCESS = true;
    /** Opposite of {@link #SUCCESS}. */
    public static final boolean FAILURE = false;

    /**
     * Useful for {@link String} operations, which return an index of <tt>-1</tt> when an item is not found.
     */
    public static final int NOT_FOUND = -1;

    /** System property - <tt>line.separator</tt> */
    public static final String NEW_LINE = System.getProperty("line.separator");
    /** System property - <tt>file.separator</tt> */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /** System property - <tt>path.separator</tt> */
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String TAB = "\t";
    public static final String SINGLE_QUOTE = "'";
    public static final String PERIOD = ".";
    public static final String DOUBLE_QUOTE = "\"";

    // PRIVATE //

    /**
     * The caller references the constants using <tt>Consts.EMPTY_STRING</tt>, and so on. Thus, the caller should be
     * prevented from constructing objects of this class, by declaring this private constructor.
     */
    private Consts() {
        // this prevents even the native class from
        // calling this ctor as well :
        throw new AssertionError();
    }
}
