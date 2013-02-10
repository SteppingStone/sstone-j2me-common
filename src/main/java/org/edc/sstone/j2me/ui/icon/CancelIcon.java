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

import org.edc.sstone.j2me.ui.util.GraphicsFunc;

/**
 * Looks like a stop sign (octagon) with an X in the middle. You should paint this with
 * width=height, but you do not have to.
 * 
 * @author Greg Orlowski
 * 
 */
public class CancelIcon implements VectorIcon {

    boolean drawOutline;

    private static int RED = 0xBB0000;
    private static int DARK_RED = 0x550000;

    public CancelIcon() {
        this(false);
    }

    public CancelIcon(boolean drawOutline) {
        this.drawOutline = drawOutline;
    }

    public void paint(Graphics g, int x, int y, int width, int height) {
        while (width % 3 > 0) {
            width--;
            x++;
        }
        while (height % 3 > 0) {
            height--;
            y++;
        }

        int lx = (width / 3);
        int ly = (height / 3);

        g.setColor(RED); // red

        // left trapezoid, starting from top-right corner going clockwise
        GraphicsFunc.fillQuadrilateral(g,
                x + lx, y,
                x + lx, y + height,
                x, y + (height - ly),
                x, y + ly);

        // right trapezoid, starting from top-left going clockwise
        GraphicsFunc.fillQuadrilateral(g,
                x + (width - lx), y,
                x + width, y + ly,
                x + width, y + (height - ly),
                x + (width - lx), y + height
                );

        // center rectangle, top-left corner
        g.fillRect(x + lx, y, lx, height);

        paintX(g, x, y, width, height);

        if (drawOutline) {
            drawOutline(g, x, y, width, height);
        }
    }

    private void drawOutline(Graphics g, int x, int y, int width, int height) {
        int lx = (width / 3);
        int ly = (height / 3);

        g.setColor(DARK_RED);

        // top line, proceeding clockwise
        g.drawLine(x += lx, y, x += lx, y);
        g.drawLine(x, y, x += lx, y += ly);
        g.drawLine(x, y, x, y += ly);
        g.drawLine(x, y, x -= lx, y += ly);

        g.drawLine(x, y, x -= lx, y);
        g.drawLine(x, y, x -= lx, y -= ly);
        g.drawLine(x, y, x, y -= ly);
        g.drawLine(x, y, x += lx, y -= ly);
    }

    // paint the x in the middle
    private void paintX(Graphics g, int x, int y, int width, int height) {

        int pad = (width > 14) ? 2 : 1;
        int lx = (width / 4) + pad;
        int ly = (height / 4) + pad;

        g.setColor(0xFFFFFF); // white

        for (int i = 0; i < 2; i++) {
            g.drawLine(x + lx + i, y + ly, x + (width - lx) + i - 1, y + (height - ly));
            g.drawLine(x + lx + i, y + (height - ly), x + (width - lx) + i - 1, y + ly);
        }
    }
}
