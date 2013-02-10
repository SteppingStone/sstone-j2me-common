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
 * This will symbolize an exit action
 * 
 * @author Greg Orlowski
 */
public class ExitIcon implements VectorIcon {

    private int corridorColor = 0x424242;
    private int doorColor = 0xBBBBBB;
    // private int arrowColor = 0xD73434;
    private int arrowColor = 0x993232;
    // private int knobColor = 0xB09E64;
    private int knobColor = 0x827444;
    private int doorFrameColor = 0x3A3A3A;

    public ExitIcon() {
    }

    public void paint(Graphics g, int x, int y, int width, int height) {
        // paintDoorFrame(g, x, y, width, height);
        paintCorridor(g, x, y, width, height);
        paintDoor(g, x, y, width, height);
        paintArrow(g, x, y, width, height);
    }

    private void paintArrow(Graphics g, int x, int y, int width, int height) {

        int arrowHeight = height / 2;
        arrowHeight = arrowHeight % 2 == 0 ? arrowHeight - 1 : arrowHeight;

        int x1 = x + width - 1, y1 = y + (height / 4);
        int x2 = x + width - (width / 2), y2 = y + (height / 2);
        int x3 = x1, y3 = y1 + arrowHeight;

        g.setColor(doorFrameColor);
        g.fillTriangle(x1, y1, x2, y2, x3, y3);

        g.setColor(arrowColor);
        g.fillTriangle(x1 - 1, y1 + 1,
                x2 + 1, y2,
                x3 - 1, y3 - 1);

    }

    private void paintDoor(Graphics g, int x, int y, int width, int height) {
        int doorFrameWidth = getDoorFrameWidth(width);
        int doorWidth = (int) (doorFrameWidth * 0.8f);
        int leftEdge = x + doorFrameWidth - doorWidth;
        int leftSideHeight = (height / 5) * 4;

        // door
        g.setColor(doorColor);
        GraphicsFunc.fillQuadrilateral(g,
                leftEdge, y + 1,
                x + doorFrameWidth - 1, y + 1,
                x + doorFrameWidth - 1, y + height - 1,
                leftEdge, y + leftSideHeight);

        // doorknob
        int diameter = Math.max((int) (doorWidth * 0.2f), 1);
        g.setColor(knobColor);
        g.fillRoundRect(leftEdge + Math.max((int) (doorWidth * 0.1f), 1), y + (height / 3),
                diameter, diameter, diameter / 2, diameter / 2);
    }

    private void paintCorridor(Graphics g, int x, int y, int width, int height) {
        int doorFrameWidth = getDoorFrameWidth(width);
        g.setColor(corridorColor);

        // if we draw the door frame, make this smaller
        // g.fillRect(x + 1, y + 1, doorFrameWidth - 1, height - 1);

        // if no frame
        g.fillRect(x, y, doorFrameWidth, height);
    }

    private int getDoorFrameWidth(int width) {
        return (width / 5) * 4;
    }

}
