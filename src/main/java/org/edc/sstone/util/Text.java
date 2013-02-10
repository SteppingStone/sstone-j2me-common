/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.util;

import java.util.Vector;

import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.util.StdLib;
import org.edc.sstone.util.StringTokenizer;

/**
 * @author Greg Orlowski
 */
public class Text {

    public static void splitLines(IFont font, String text, Vector lines, int maxWidth) {
        splitLines(font, text, lines, null, maxWidth);
    }

    private static boolean isLineBreak(String str) {
        return "\n".equals(str) || "\r\n".equals(str) || "\r".equals(str);
    }

    public static void splitLines(IFont font, String text, Vector lines, Vector tokens, int maxWidth,
            Character syllableSeparator) {
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = null;

        st = syllableSeparator == null
                ? new StringTokenizer(text, " \t", "\n")
                : new StringTokenizer(text, " \t", "\n", "");

        int spaceWidth = font.stringWidth(" ");
        int tokenWidth = 0;
        boolean previousWasLineBreak = false;

        TOKEN_LOOP: for (String token = null; st.hasMoreTokens();) {
            token = st.nextToken();
            if (token != null) {

                if (isLineBreak(token)) {
                    if (previousWasLineBreak) {
                        lines.addElement(sb.toString());
                        sb.delete(0, sb.length());
                        lines.addElement("");
                        previousWasLineBreak = false;
                        continue TOKEN_LOOP;
                    }
                    previousWasLineBreak = true;
                    continue TOKEN_LOOP;
                } else {
                    previousWasLineBreak = false;
                }

                tokenWidth = syllableSeparator == null
                        ? font.stringWidth(token)
                        : font.stringWidth(StdLib.removeChar(token, syllableSeparator.charValue()));

                // If the token itself exceeds the width then just overrun the
                // boundaries (maybe I need to clip when rendering?)
                if (tokenWidth >= maxWidth) {
                    if (sb.length() >= 0)
                        lines.addElement(sb.toString());
                    lines.addElement(token);
                    if (tokens != null) {
                        appendTokenCoordinates(lines, tokens, token, tokenWidth);
                    }
                } else if (strWidth(sb, font, spaceWidth, tokenWidth, syllableSeparator) >= maxWidth) {
                    lines.addElement(sb.toString());

                    sb.delete(0, sb.length());

                    appendToken(sb, lines, tokens, token, tokenWidth, syllableSeparator);
                } else {
                    appendToken(sb, lines, tokens, token, tokenWidth, syllableSeparator);
                }
            }
        }

        // append leftovers
        if (sb.length() > 0) {
            lines.addElement(sb.toString());
        }
    }

    public static void splitLines(IFont font, String text, Vector lines, Vector tokens, int maxWidth) {
        splitLines(font, text, lines, tokens, maxWidth, null);
    }

    private static void appendToken(StringBuffer sb, Vector lines, Vector tokens, String token,
            int tokenWidth, Character syllableSeparator) {

        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '-') {
            sb.append(' ');
        }
        if (tokens != null) {
            appendTokenCoordinates(sb, lines, tokens, token, tokenWidth, syllableSeparator);
        }
        sb.append(stripSyllableSeparator(token, syllableSeparator));
    }

    static String stripSyllableSeparator(String token, Character syllableSeparator) {
        if (syllableSeparator == null)
            return token;
        return StdLib.removeChar(token, syllableSeparator.charValue());
    }

    private static int strWidth(StringBuffer sb, IFont font, int spaceWidth, int tokenWidth, Character syllableSeparator) {
        int width = (syllableSeparator == null
                ? font.stringWidth(sb.toString())
                : font.stringWidth(StdLib.removeChar(sb.toString(), syllableSeparator.charValue()))) + tokenWidth;

        // TODO: remove the hard-coded '-', which is here b/c we are calculating lines that may have
        // a hyphenated word at the end.
        return (sb.length() > 0 && sb.charAt(sb.length() - 1) == '-')
                ? width
                : width + spaceWidth;
    }

    static void appendTokenCoordinates(StringBuffer sb, Vector lines, Vector tokens, String token, int tokenWidth,
            Character syllableSeparator) {
        int startIdx = sb.length();
        tokens.addElement(new Token(token, lines.size(), startIdx,
                startIdx + stripSyllableSeparator(token, syllableSeparator).length(), tokenWidth));
    }

    private static void appendTokenCoordinates(Vector lines, Vector tokens, String token, int tokenWidth) {
        tokens.addElement(new Token(token, lines.size() - 1, 0, token.length(), tokenWidth));
    }

    public static class Token {
        public final int lineIdx;
        public final int startPos;
        public final int endPos;
        public final int tokenWidth;
        public final String text;

        public Token(String text, int lineIdx, int startPos, int endPos, int tokenWidth) {
            this.text = text;
            this.lineIdx = lineIdx;
            this.startPos = startPos;
            this.endPos = endPos;
            this.tokenWidth = tokenWidth;
        }
    }

}
