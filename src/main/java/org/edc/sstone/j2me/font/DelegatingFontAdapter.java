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

import org.edc.sstone.j2me.ui.util.GraphicsFunc;
import org.edc.sstone.ui.model.FontStyle;

/**
 * @author Greg Orlowski
 */
public class DelegatingFontAdapter implements IFont {

    public boolean strikeThrough = false;
    private IFont delegateFont;

    DelegatingFontAdapter(IFont ifont, FontStyle fontStyle) {
        this.delegateFont = ifont;
        this.strikeThrough = fontStyle.isStrikeThrough();
    }

    public void drawChar(Graphics g, char character, int x, int y, int anchor) {
        delegateFont.drawChar(g, character, x, y, anchor);
        if (strikeThrough) {
            GraphicsFunc.strikeThrough(g, this, x, y, anchor, charWidth(character));
        }
    }

    public void drawChars(Graphics g, char[] data, int offset, int length, int x, int y, int anchor) {
        delegateFont.drawChars(g, data, offset, length, x, y, anchor);
        if (strikeThrough) {
            GraphicsFunc.strikeThrough(g, this, x, y, anchor, charsWidth(data, offset, length));
        }
    }

    public void drawString(Graphics g, String str, int x, int y, int anchor) {
        delegateFont.drawString(g, str, x, y, anchor);
        if (strikeThrough) {
            GraphicsFunc.strikeThrough(g, this, x, y, anchor, stringWidth(str));
        }
    }

    public void drawSubstring(Graphics g, String str, int offset, int len, int x, int y, int anchor) {
        delegateFont.drawSubstring(g, str, offset, len, x, y, anchor);
        if (strikeThrough) {
            GraphicsFunc.strikeThrough(g, this, x, y, anchor, stringWidth(str.substring(offset, len)));
        }
    }

    public int charsWidth(char[] ch, int offset, int length) {
        return delegateFont.charsWidth(ch, offset, length);
    }

    public int charWidth(char ch) {
        return delegateFont.charWidth(ch);
    }

    public int getHeight() {
        return delegateFont.getHeight();
    }

    public int getStyle() {
        return delegateFont.getStyle();
    }

    public int stringWidth(String str) {
        return delegateFont.stringWidth(str);
    }

    public int substringWidth(String str, int offset, int len) {
        return delegateFont.substringWidth(str, offset, len);
    }

}
