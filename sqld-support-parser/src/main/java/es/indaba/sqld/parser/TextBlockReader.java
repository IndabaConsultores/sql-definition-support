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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextBlockReader {

    public static final String NEW_LINE = System.getProperty("line.separator");

    private static final String fCOMMENT = "--";

    private static final String fEND_BLOCK = "}";

    private static final String fSTART_BLOCK = "{";
    private static final Pattern fKEY_NAME_PATTERN = Pattern.compile(Regex.SIMPLE_SCOPED_IDENTIFIER);
    private static final Pattern fSUBSTITUTION_PATTERN =
            Pattern.compile("(?:\\$\\{)" + Regex.SIMPLE_IDENTIFIER + "(?:\\})");

    private static final String fCONSTANTS_BLOCK = "constants"; // ignore case
    private static final String fCONSTANTS_SEPARATOR = "=";

    private static final int fNOT_FOUND = -1;

    private static final Logger fLogger = Util.getLogger(TextBlockReader.class);

    // PRIVATE //
    private final LineNumberReader fReader;

    private final String fConfigFileName;
    private StringBuilder fBlockBody;
    /*
     * These two distinguish between regular blocks and 'constants' blocks. sql statements, and those for constants.
     */
    private boolean fIsReadingBlockBody;
    private boolean fIsReadingConstants;
    /**
     * For regular Blocks, refers to the Block Name. For constants, refers not to the name of the containing block
     * (which is always the same), but to the identifier for the constant itself; this is line-level, not block-level.
     */
    private String fKey;
    /**
     * Remembers where the current block started. Used for debugging messages.
     */
    private int fBlockStartLineNumber;
    /**
     * Stores the constants, which are always intended for substitutions appearing later in the file. These items are
     * not added to the final Properties object returned by this class; rather, they assist in performing substitutions
     * of simple constants.
     * 
     * <P>
     * There can be multiple Constants blocks in a TEXT_BLOCK file. This item collects them all into one data structure.
     */
    private final Properties fConstants;

    /**
     * @param aInput has an underlying <tt>TextBlock</tt> file as source
     * @param aConfigFileName the underlying source file name
     */
    public TextBlockReader(InputStream aInput, String aConfigFileName) {
        fReader = new LineNumberReader(new InputStreamReader(aInput));
        fConfigFileName = aConfigFileName;
        fConstants = new Properties();
    }

    private void addConstant(String aLine) {
        int idxFirstSeparator = aLine.indexOf(fCONSTANTS_SEPARATOR);
        if (idxFirstSeparator == fNOT_FOUND) {
            String message = reportLineNumber() + "Cannot find expected constants separator character : "
                    + Util.quote(fCONSTANTS_SEPARATOR);
            throw new IllegalAccessError(message);
        }
        String key = verifiedKeyName(aLine.substring(0, idxFirstSeparator).trim());
        String value = aLine.substring(idxFirstSeparator + 1).trim();
        addToResult(key, value, fConstants);
    }

    private void addLine(String aLine) {
        if (fIsReadingBlockBody) {
            addToBlockBody(aLine);
        } else if (fIsReadingConstants) {
            addConstant(aLine);
        }
    }

    private void addToBlockBody(String aLine) {
        fBlockBody.append(aLine);
        fBlockBody.append(NEW_LINE);
    }

    /**
     * Allow a duplicate key, but log as SEVERE.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addToResult(String aKey, String aValue, Map aResult) {
        if (aResult.containsKey(aKey)) {
            fLogger.severe("DUPLICATE Value found for this Block Name or constant name " + Util.quote(aKey)
                    + ". This almost always indicates an error.");
        }
        aResult.put(aKey, aValue);
    }

    private void endBlock(Properties aResult) {
        if (fIsReadingBlockBody) {
            String finalBlockBody = resolveAnySubstitutions(fBlockBody.toString().trim(), aResult);
            addToResult(fKey, finalBlockBody, aResult);
        }
        fIsReadingBlockBody = false;
        fIsReadingConstants = false;
        fBlockStartLineNumber = 0;
    }

    private String getBlockName(String aLine) {
        int indexOfBrace = aLine.indexOf(fSTART_BLOCK);
        if (indexOfBrace == -1) {
            throw new IllegalArgumentException(
                    reportLineNumber() + "Expecting to find line defining a block, containing a trailing "
                            + Util.quote(fSTART_BLOCK) + ". Found this line instead : " + Util.quote(aLine));
        }
        String candidateKey = aLine.substring(0, indexOfBrace).trim();
        return verifiedKeyName(candidateKey);
    }

    private String getReplacement(Matcher aMatcher, Properties aProperties) {
        String replacementKey = aMatcher.group(1);
        // the replacement may be another Block Body, or it may be a constant
        String replacement = aProperties.getProperty(replacementKey);
        if (!Util.textHasContent(replacement)) {
            replacement = fConstants.getProperty(replacementKey);
        }
        if (!Util.textHasContent(replacement)) {
            throw new IllegalArgumentException(reportBlockStartLineNumber() + "The substitution variable ${"
                    + replacementKey + "} is not defined. Substitution variables must be defined before they "
                    + "are referenced. Please correct the ordering problem.");
        }
        fLogger.finest("Replacement for " + Util.quote(replacementKey) + " is " + Util.quote(replacement));
        return replacement;
    }

    private boolean isComment(String aLine) {
        return aLine.trim().startsWith(fCOMMENT);
    }

    private boolean isEndOfBlock(String aLine) {
        return fEND_BLOCK.equals(aLine.trim());
    }

    private boolean isIgnorable(String aLine) {
        boolean result;
        if (isInBlock()) {
            // no empty lines within blocks allowed
            result = isComment(aLine);
        } else {
            result = !Util.textHasContent(aLine) || isComment(aLine);
        }
        return result;
    }

    private boolean isInBlock() {
        return fIsReadingBlockBody || fIsReadingConstants;
    }

    /*
     * Parse the underlying <tt>TEXT_BLOCK</tt> file into a {@link Properties} object, which uses key-value pairs of
     * <tt>String</tt>s.
     * 
     * <P>
     * Using this example entry in a <tt>*.sql</tt> file :
     * 
     * <PRE>
     *    FETCH_NUM_MSGS_FOR_USER {
     *      SELECT COUNT(Id) FROM MyMessage WHERE LoginName=?
     *    }
     * </PRE>
     * 
     * the key is
     * <P>
     * <tt>FETCH_NUM_MSGS_FOR_USER</tt>
     * <P>
     * while the value is
     * <P>
     * <tt>SELECT COUNT(Id) FROM MyMessage WHERE LoginName=?</tt>.
     */
    public Properties read() throws IOException {
        fLogger.fine("Reading text block file : " + Util.quote(fConfigFileName));
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
                    addLine(line);
                } else {
                    endBlock(result);
                }
            }
        }
        return result;
    }

    private String reportBlockStartLineNumber() {
        return "[" + fConfigFileName + ":" + Integer.toString(fBlockStartLineNumber) + "] ";
    }

    private String reportLineNumber() {
        return "[" + fConfigFileName + ":" + Integer.toString(fReader.getLineNumber()) + "] ";
    }

    private String resolveAnySubstitutions(String aRawBlockBody, Properties aResult) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = fSUBSTITUTION_PATTERN.matcher(aRawBlockBody);
        while (matcher.find()) {
            matcher.appendReplacement(result, getReplacement(matcher, aResult));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private void startBlock(String aLine) {
        fBlockStartLineNumber = fReader.getLineNumber();
        fKey = getBlockName(aLine);
        if (fCONSTANTS_BLOCK.equalsIgnoreCase(fKey)) {
            fIsReadingConstants = true;
            fIsReadingBlockBody = false;
        } else {
            fBlockBody = new StringBuilder();
            fIsReadingBlockBody = true;
            fIsReadingConstants = false;
        }
    }

    private String verifiedKeyName(String aCandidateKey) {
        if (Util.matches(fKEY_NAME_PATTERN, aCandidateKey)) {
            return aCandidateKey;
        }
        String message =
                reportLineNumber() + "The name " + Util.quote(aCandidateKey) + " is not in the expected syntax. "
                        + "It does not match the regular expression " + fKEY_NAME_PATTERN.pattern();
        throw new IllegalArgumentException(message);
    }
}
