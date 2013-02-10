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
package org.edc.sstone.j2me.font;

import java.util.Vector;

import org.edc.sstone.ui.model.Dimension;

/*
 * TODO: move TextFunc functions and remove this class (?)
 */
/**
 * @author Greg Orlowski
 */
public class TextFunc {

    public static Dimension calculateSize(IFont font, float lineHeight, Vector lines, boolean addSpaceAboveFirstLine) {
        /*
         * note that we ALWAYS round the lineHeight * fontHeight DOWN... so, e.g., 1.1f * 18px =
         * 19.8f, which gets rounded down to 19px, resulting in a line spacing of 1px
         */
        int lineSpacing = ((int) (lineHeight * font.getHeight())) - font.getHeight();
        int height = getSegmentHeight(font, lineSpacing, lines.size(), addSpaceAboveFirstLine);
        int width = 0;
        for (int i = 0; i < lines.size(); i++) {
            width = Math.max(width, font.stringWidth(getLine(lines, i)));
        }
        return new Dimension(width, height);
    }

    private static String getLine(Vector lines, int i) {
        return (String) lines.elementAt(i);
    }

    private static int getSegmentHeight(IFont font, int lineSpacing, int count, boolean addSpaceAboveFirstLine) {
        if (addSpaceAboveFirstLine) {
            return count * (font.getHeight() + lineSpacing);
        }

        if (count == 0)
            return 0;
        return ((font.getHeight() + lineSpacing) * count) - lineSpacing;
    }
}
