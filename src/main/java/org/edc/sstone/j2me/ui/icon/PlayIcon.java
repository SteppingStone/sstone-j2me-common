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
public class PlayIcon implements VectorIcon {

    public void paint(Graphics g, int x, int y, int width, int height) {
        // consider a square not a circle? It would give us more
        // room for the triangle
        g.setColor(0x354F34); // a dark green
        g.drawArc(x, y, width, height, 180, 360);

        g.setColor(0x5A9D58); // a medium green
        g.fillArc(x, y, width, height, 180, 360);

        int triWidth = width / 3;
        int triHeight = height / 3;

        // 40% looks OK at both small and larger sizes
        x += (int) ((float) width * 0.4f);

        g.setColor(0xEEEEEE); // off-white
        g.fillTriangle(x, y + triHeight,
                x + triWidth, y + (height / 2),
                x, y + (height - triHeight));
    }
}
