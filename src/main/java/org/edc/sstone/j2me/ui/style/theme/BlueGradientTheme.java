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
package org.edc.sstone.j2me.ui.style.theme;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.font.FontFactory;
import org.edc.sstone.j2me.ui.util.GraphicsFunc;

/**
 * @author Greg Orlowski
 */
public class BlueGradientTheme extends Theme {

    public BlueGradientTheme(FontFactory fontFactory, int screenWidth, int screenHeight) {
        super(fontFactory, screenWidth, screenHeight);
    }

    public void paintTitleBarBackground(Graphics g) {
        int primary = getTitleBarBackgroundColor();
        int secondary = getTitleBarHighlightColor();

        GraphicsFunc.gradientFill(g, 0, 0, screenWidth, getTitleBarHeight(),
                true, primary, secondary, 0.2f);
    }

    protected void paintMenubarBackground(Graphics g) {
        // g.setColor(getMenubarBackgroundColor());
        // g.fillRect(0, getMenubarTop(), screenWidth, menubarHeight);

        int primary = getMenubarBackgroundColor();
        int secondary = getMenubarHighlightColor();

        GraphicsFunc.gradientFill(g, 0, getMenubarTop(), screenWidth, getMenubarHeight(),
                true, primary, secondary, 0.2f);

        paintMenubarButtonDividers(g);
    }

    public int getMenubarHighlightColor() {
        return 0x5E7EA5; // steel blue
    }

    public int getMenubarBackgroundColor() {
        return 0x33344C; // navy
    }

    public int getScrollbarThumbColor() {
        // return 0xAAAAAA;
        // return 0x737ACA;
        // return getPopupMenuBackgroundColor();
        return 0xB4B4E0;
    }

    protected void paintScrollbarThumb(Graphics g, int scrollbarThumbTop, int scrollbarThumbHeight) {
        super.paintScrollbarThumb(g, scrollbarThumbTop, scrollbarThumbHeight);
        paintTrackBarGrip(g, scrollbarThumbTop, scrollbarThumbHeight);
        paintTrackBarBorder(g, scrollbarThumbTop, scrollbarThumbHeight);
        // g.setColor(getScrollBarTrackBarColor());
        // g.fillRect(scrollBarLeft + 1, scrollbarThumbTop, scrollBarWidth - 1,
        // scrollbarThumbHeight);
    }

    private void paintTrackBarBorder(Graphics g, int scrollbarThumbTop, int scrollbarThumbHeight) {
        g.setColor(0x5153A5);
        g.drawLine(scrollBarLeft, scrollbarThumbTop, scrollBarLeft, scrollbarThumbTop + scrollbarThumbHeight);
    }

    /**
     * Draw a "grip" in the middle of the track bar
     * 
     * @param g
     * @param scrollbarThumbTop
     * @param scrollbarThumbHeight
     */
    private void paintTrackBarGrip(Graphics g, int scrollbarThumbTop, int scrollbarThumbHeight) {
        if (scrollbarThumbHeight > 5) {
            // g.setColor(0x333333);
            g.setColor(getMenubarBackgroundColor());
            // g.setColor(0x00EE00);
            // int y = scrollbarThumbTop + ((scrollbarThumbTop + scrollbarThumbHeight) / 2);
            int y = scrollbarThumbTop + (scrollbarThumbHeight / 2);

            if (scrollbarThumbHeight > 9) {
                for (int n = -1; n <= 1; n++)
                    g.drawLine(scrollBarLeft + 1, y + (n * 2), scrollBarLeft + scrollBarWidth - 1, y + (n * 2));
            } else {
                g.drawLine(scrollBarLeft + 1, y, scrollBarLeft + scrollBarWidth - 1, y);
            }
        }
    }

    public int getPopupMenuBackgroundColor() {
        return 0xC1C1FF;
    }

    public int getMenubarIconColor() {
        // return 0x65FF66;
        // return 0x63BC5A;
        // return 0x55E147;
        return 0x4DE164;
    }

}
