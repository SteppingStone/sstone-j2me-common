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
public class CheckBoxIcon implements VectorIcon {

    public void paint(Graphics g, int x, int y, int width, int height) {

        float sizePercent = 0.85f;
        int h = (int) (height * sizePercent);
        int w = (int) (width * sizePercent);

        x += (width - w) / 2;
        y += (height - h) / 2;

        g.setColor(0x61C35E); // green
        g.fillRoundRect(x, y, w, h, width - w, height - h);

        g.setColor(0x2D592B); // dark green
        g.drawRoundRect(x, y, w, h, width - w - 1, height - h - 1);

        g.setColor(0xFFFFFF); // white

        int y1 = y + (int) (h * 0.6f);

        for (int i = 0; i < 2; i++) {
            g.drawLine(x + (w / 5) + i + 1, y1,
                    x + (w / 2) + i, y + h - (h / 6) - 1);

            g.drawLine(x + (w / 2) + i, y + h - (h / 6) - 1,
                    x + w - (w / 5) + i - 2, y + (h / 4));

            // TODO: I could make this an arc
            // g.drawArc(x, y, width, height, startAngle, arcAngle)
        }
    }
}
