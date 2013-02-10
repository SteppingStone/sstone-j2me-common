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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.core.Registry;

/**
 * @author Greg Orlowski
 */
public class ArrowHeadIcon implements VectorIcon {

    private final int direction;

    // Out of color range
    private int color = -1;

    /**
     * @param direction
     *            Should be one of {@link Canvas#UP}, {@link Canvas#LEFT}, {@link Canvas#RIGHT}
     */
    public ArrowHeadIcon(int direction) {
        this(direction, Registry.getManager().getTheme().getMenubarIconColor());
    }

    public ArrowHeadIcon(int direction, int color) {
        super();
        this.direction = direction;
        this.color = color;
    }

    public void paint(Graphics g, int x, int y, int width, int height) {

        int ax = 0, bx = 0, cx = 0;
        int ay = 0, by = 0, cy = 0;

        int yOffset = 0;

        switch (direction) {
            case Canvas.LEFT:
                x += (width / 4);
                // yOffset = Math.max(height / 10, 1);

                ax = (width / 2);
                bx = 0;
                cx = (width / 2);

                ay = 0 + yOffset;
                by = (height / 2) + yOffset;
                cy = (height - 1) + yOffset;
                break;

            case Canvas.RIGHT:
                x += (width / 4);
                // yOffset = Math.max(height / 9, 1);
                // yOffset = (int) (height * 0.10f);

                ax = 0;
                bx = (width / 2);
                cx = 0;

                ay = 0 + yOffset;
                by = (height / 2) + yOffset;
                cy = height - 1 + yOffset;
                break;

            case Canvas.UP:
                yOffset = (height / 3);
                width *= 1.2f;
                width = width % 2 == 0 ? width + 1 : width;
                height = width;

                ax = 0;
                bx = (width / 2);
                cx = width - 1;

                ay = (height / 2) + yOffset;
                by = 0 + yOffset;
                cy = ay;
                break;
        }
        g.setColor(color);
        g.fillTriangle(x + ax, y + ay, x + bx, y + by, x + cx, y + cy);
    }

}
