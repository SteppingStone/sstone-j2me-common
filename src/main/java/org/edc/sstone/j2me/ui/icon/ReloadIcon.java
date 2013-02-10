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
public class ReloadIcon implements VectorIcon {

    public void paint(Graphics g, int x, int y, int width, int height) {
        // drawSquarishReload(g, x, y, width, height);
        drawUpArrow(g, x, y, width, height);
    }

    static int reduceToEven(int width) {
        while (width >= 2 && width % 2 != 0)
            width--;
        return width;
    }

    static int reduceToOdd(int width) {
        while (width >= 2 && width % 2 != 1)
            width--;
        return width;
    }

    private void drawUpArrow(Graphics g, int x, int y, int width, int height) {
        int bgColor = 0xDADADA; // gray
        int fgColor = g.getColor(); // the theme should set the color on paint

        /*
         * The width should be even. If width is even and the left-vertex of the up-triangle is at x
         * and the right is at x+width then the number of horizontal pixels will be odd, which will
         * result in a more-symmetrical isosceles triangle.
         */
        width = reduceToOdd(width);
        g.setColor(bgColor);
        g.fillRect(x, y, width, height);

        int midpoint = x + (width / 2);

        // Log.debug("WIDTH: " + width);

        int triangleWidth = reduceToEven(width - 2); // triangleWidth(width);
        int triangleHeight = triangleWidth / 2;
        y++;

        // Log.debug("triangleWidth: " + triangleWidth + "; width: " + width);
        // Log.debug("triangleHeight: " + triangleHeight );

        int rectangleHalfWidth = Math.max(triangleWidth / 10, 1);

        g.setColor(fgColor);
        g.fillRect(midpoint - rectangleHalfWidth, y + (height / 3),
                (rectangleHalfWidth * 2) + 1, ((height / 3) * 2) - 1);

        // Fillrect is messed up. draw lines instead.
        // for (int lx = midpoint - rectangleHalfWidth; lx < midpoint + rectangleHalfWidth; lx++) {
        // g.drawLine(lx, y + (height / 3), lx, y + height - 3);
        // }

        // g.drawLine(midpoint, y + (height / 3), midpoint, y + (((height / 3) * 2) - 1));

        int[][] vertices = new int[][] {
                { x + 1, y + triangleHeight },
                /*
                 * The WTK emulator renders triangles badly. It is only really visible on very small
                 * triangles. On WTK, the triangle looks isosceles if it has an even number of
                 * horizontal points, which makes no sense.
                 * 
                 * { x + 2 + triangleWidth, y + triangleHeight },
                 */
                { x + 1 + triangleWidth, y + triangleHeight },
                { midpoint, y }
        };

        // Log.debug(ArrayUtil.matrixToString(vertices));

        g.fillTriangle(vertices[0][0], vertices[0][1],
                vertices[1][0], vertices[1][1],
                vertices[2][0], vertices[2][1]);
    }

}
