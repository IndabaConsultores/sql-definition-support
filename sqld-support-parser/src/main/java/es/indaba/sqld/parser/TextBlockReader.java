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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class TextBlockReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextBlockReader.class);

    /*
     * Scoped identifier.
     * 
     * <P> Either two {@link #SIMPLE_IDENTIFIER}s separated by a period, or a single {@link #SIMPLE_IDENTIFIER}. The
     * item before the period represents an <em>optional</em> scoping qualifier. (This style is used by SQL statement
     * identifiers, where the scoping qualifier represents the target database.)
     */
    private static final String SIMPLE_SCOPED_IDENTIFIER = "(?:[a-zA-Z_]+[a-zA-Z_0-9]*\\.)?(?:[a-zA-Z_]+[a-zA-Z_0-9]*)";


    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String COMMENT = "--";

    private static final String END_BLOCK = "}";

    private static final String START_BLOCK = "{";
    private static final Pattern KEY_NAME_PATTERN = Pattern.compile(SIMPLE_SCOPED_IDENTIFIER);


    // PRIVATE //
    private final LineNumberReader fReader;

    private final String fConfigFileName;
    private StringBuilder fBlockBody;
    /*
     * These two distinguish between regular blocks and 'constants' blocks. sql statements, and those for constants.
     */
    private boolean fIsReadingBlockBody;

    /**
     * For regular Blocks, refers to the Block Name. For constants, refers not to the name of the containing block
     * (which is always the same), but to the identifier for the constant itself; this is line-level, not block-level.
     */
    private String fKey;

    /**
     * @param aInput has an underlying <tt>TextBlock</tt> file as source
     * @param aConfigFileName the underlying source file name
     */
    public TextBlockReader(final InputStream aInput, final String aConfigFileName) {
        fReader = new LineNumberReader(new InputStreamReader(aInput));
        fConfigFileName = aConfigFileName;
    }


    private void addToBlockBody(final String aLine) {
        fBlockBody.append(aLine);
        fBlockBody.append(NEW_LINE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addToResult(final String aKey, final String aValue, final Map aResult) {
        if (aResult.containsKey(aKey)) {
            LOGGER.error("DUPLICATE Value found for this Block Name '{}' in {}", aKey, fConfigFileName);
            throw new IllegalArgumentException("DUPLICATE Value found for this Block Name '" + aKey + "'");
        }
        aResult.put(aKey, aValue);
    }

    private void endBlock(final Properties aResult) {
        if (fIsReadingBlockBody) {
            addToResult(fKey, fBlockBody.toString().trim(), aResult);
        }
        fIsReadingBlockBody = false;
    }

    private String getBlockName(final String aLine) {
        final int indexOfBrace = aLine.indexOf(START_BLOCK);
        if (indexOfBrace == -1) {
            LOGGER.error(
                    "Error parsing file: {} Expecting to find line defining a block, containing a trailing '{}'.Found this: '{}'",
                    reportLineNumber(), START_BLOCK, aLine);
            throw new IllegalArgumentException(
                    reportLineNumber() + "Expecting to find line defining a block, containing a trailing '"
                            + START_BLOCK + "'. Found this line instead : '" + aLine + "'");
        }
        final String candidateKey = aLine.substring(0, indexOfBrace).trim();
        return verifiedKeyName(candidateKey);
    }



    private boolean isComment(final String aLine) {
        return aLine.trim().startsWith(COMMENT);
    }

    private boolean isEndOfBlock(final String aLine) {
        return END_BLOCK.equals(aLine.trim());
    }

    private boolean isIgnorable(final String aLine) {
        boolean result;
        if (isInBlock()) {
            // no empty lines within blocks allowed
            result = isComment(aLine);
        } else {
            result = StringUtils.isBlank(aLine) || isComment(aLine);
        }
        return result;
    }

    private boolean isInBlock() {
        return fIsReadingBlockBody;
    }

    /*
     * Parse the underlying <tt>TEXT_BLOCK</tt> file into a {@link Properties} object, which uses key-value pairs of
     * <tt>String</tt>s.
     * 
     * <P> Using this example entry in a <tt>*.sql</tt> file :
     * 
     * <PRE> FETCH_NUM_MSGS_FOR_USER { SELECT COUNT(Id) FROM MyMessage WHERE LoginName=? } </PRE>
     * 
     * the key is <P> <tt>FETCH_NUM_MSGS_FOR_USER</tt> <P> while the value is <P> <tt>SELECT COUNT(Id) FROM MyMessage
     * WHERE LoginName=?</tt>.
     */
    public Properties read() throws IOException {
        LOGGER.debug("Reading text block file : '{}'", fConfigFileName);
        final Properties result = new Properties();
        String line = null;
        while ((line = fReader.readLine()) != null) {
            if (isIgnorable(line)) {
                continue;
            }
            if (!isInBlock()) {
                startBlock(line);
            } else {
                if (!isEndOfBlock(line)) {
                    addToBlockBody(line);
                } else {
                    endBlock(result);
                }
            }
        }
        return result;
    }

    private String reportLineNumber() {
        return "[" + fConfigFileName + ":" + Integer.toString(fReader.getLineNumber()) + "] ";
    }


    private void startBlock(final String aLine) {
        fKey = getBlockName(aLine);
        fBlockBody = new StringBuilder();
        fIsReadingBlockBody = true;
    }

    private String verifiedKeyName(final String aCandidateKey) {
        if (matches(KEY_NAME_PATTERN, aCandidateKey)) {
            return aCandidateKey;
        }
        final String message = reportLineNumber() + "The name '" + aCandidateKey + "' is not in the expected syntax. "
                + "It does not match the regular expression " + KEY_NAME_PATTERN.pattern();
        throw new IllegalArgumentException(message);
    }

    /*
     * Return true only if <tt>aText</tt> is non-null, and matches <tt>aPattern</tt>.
     * 
     * <P> Differs from {@link Pattern#matches} and {@link String#matches}, since the regex argument is a compiled
     * {@link Pattern}, not a <tt>String</tt>.
     */
    public static boolean matches(final Pattern aPattern, final String aText) {
        /*
         * Implementation Note: Patterns are thread-safe, while Matchers are not. Thus, a Pattern may be compiled by a
         * class once upon startup, then reused safely in a multi-threaded environment.
         */
        if (aText == null)
            return false;
        final Matcher matcher = aPattern.matcher(aText);
        return matcher.matches();
    }
}
