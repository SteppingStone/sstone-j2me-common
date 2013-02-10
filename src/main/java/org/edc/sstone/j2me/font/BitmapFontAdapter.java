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

import javax.microedition.lcdui.Graphics;

import net.sergetk.mobile.lcdui.BitmapFont;

/**
 * @author Greg Orlowski
 */
class BitmapFontAdapter implements IFont {

    final BitmapFont font;

    BitmapFontAdapter(BitmapFont font) {
        this.font = font;
    }

    public void drawChar(Graphics g, char character, int x, int y, int anchor) {
        font.drawChar(g, character, x, y);
    }

    public void drawChars(Graphics g, char[] data, int offset, int length, int x, int y, int anchor) {
        font.drawChars(g, data, offset, length, x, y, anchor);

    }

    public void drawString(Graphics g, String str, int x, int y, int anchor) {
        font.drawString(g, str, x, y, anchor);
    }

    public void drawSubstring(Graphics g, String str, int offset, int len, int x, int y, int anchor) {
        font.drawSubstring(g, str, offset, len, x, y, anchor);
    }

    public int charsWidth(char[] ch, int offset, int length) {
        return font.charsWidth(ch, offset, length);
    }

    public int charWidth(char ch) {
        return font.charWidth(ch);
    }

    public int getHeight() {
        return font.getHeight();
    }

    public int getStyle() {
        return font.getStyle();
    }

    public int stringWidth(String str) {
        return font.stringWidth(str);
    }

    public int substringWidth(String str, int offset, int len) {
        return font.substringWidth(str, offset, len);
    }

}
