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
package org.edc.sstone.j2me.ui.icon;

import javax.microedition.lcdui.Graphics;

/**
 * @author Greg Orlowski
 */
public class PauseIcon implements VectorIcon {

    public void paint(Graphics g, int x, int y, int width, int height) {

        // If width + height are not even, the center cannot be centered. At low-res,
        // this looks terrible
        if (width % 2 != 0) {
            width--;
            x++;
        }
        if (height % 2 != 0) {
            height--;
            y++;
        }

        // this is just slightly rounded.
        int arcWidth = width / 2;
        int arcHeight = height / 2;

        // this is very rounded (looks like a circle)
        // int arcWidth = width;
        // int arcHeight= height;

        // dark gray outline (with round rect, the left edge gets chopped. do not paint a border
        // g.setColor(0x444444);
        // g.drawRoundRect(x, y, width, height,
        // arcWidth, arcHeight);

        // A rounded-rectangle with a centered square ends up looking better than a circle+centered
        // square at low-res b/c we need to do less fussing to ensure centering of a
        // square-in-square
        // than a square-in-circle
        g.setColor(0x999999); // med gray
        g.fillRoundRect(x, y, width, height,
                arcWidth, arcHeight); // rounded-rectangle

        // paintSquareAndCenter(g, x, y, width, height);
        paintLeftRight(g, x, y, width, height);
    }

    // private void paintSquareAndCenter(Graphics g, int x, int y, int width, int height) {
    // int halfWidth = width / 2;
    // int halfHeight = height / 2;
    //
    // g.fillRect(x + (width / 4), y + (height / 4),
    // halfWidth, halfHeight);
    //
    // }

    private void paintLeftRight(Graphics g, int x, int y, int width, int height) {
        int innerWidth = Math.max(width / 5, 1);
        int innerHeight = Math.max((height / 5), 1) * 3;
        int innerTop = y + ((height - innerHeight) / 2);

        int xMid = x + (width / 2);

        // little vertical rectangles to form pause
        g.setColor(0xEEEEEE); // off-white

        // left
        g.fillRect((xMid - innerWidth) - (innerWidth / 2), innerTop,
                innerWidth, innerHeight);

        // right
        g.fillRect(xMid + (innerWidth / 2), innerTop,
                innerWidth, innerHeight);
    }

}
