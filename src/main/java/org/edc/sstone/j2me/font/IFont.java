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

/**
 * An adapter interface that supports rendering text with different concrete implementations. I'm
 * calling this IFont so it's clear in the code that it is not a [javax.microedition.lcdui.Font].
 * 
 * TODO (post v1.0): Consider replacing Graphics with an adapter in all the method signatures, which
 * would decouple IFont from J2ME and might simplify porting to other platforms (android)
 * 
 * @author Greg Orlowski
 */
public interface IFont {

    /*
     * From Graphics
     */
    public void drawChar(Graphics g, char character, int x, int y, int anchor);

    public void drawChars(Graphics g, char[] data, int offset, int length, int x, int y, int anchor);

    public void drawString(Graphics g, String str, int x, int y, int anchor);

    public void drawSubstring(Graphics g, String str, int offset, int len, int x, int y, int anchor);

    /*
     * From Font
     */
    public int charsWidth(char[] ch, int offset, int length);

    public int charWidth(char ch);

    public int getHeight();

    public int getStyle();

    public int stringWidth(String str);

    public int substringWidth(String str, int offset, int len);

}
