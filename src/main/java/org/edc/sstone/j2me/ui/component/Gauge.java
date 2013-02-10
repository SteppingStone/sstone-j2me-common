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
package org.edc.sstone.j2me.ui.component;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.Constants;
import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.ui.style.Style;

/**
 * @author Greg Orlowski
 */
public class Gauge extends AbstractHorizontalSelectComponent {

    private int count;
    private int lowerBound;
    private int upperBound;
    private int value;

    public Gauge(Style style, String title, int lowerBound, int upperBound, int count) {
        super(style, title);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.count = count;
        this.value = lowerBound;
    }

    private int getStep() {
        return (upperBound - lowerBound) / count;
    }

    public boolean changeValue(byte direction) {
        boolean ret = canChange(direction);
        int newval = value + (getStep() * direction);

        if (direction == Constants.DIRECTION_FORWARD) {
            this.value = newval > upperBound ? upperBound : newval;
        } else {
            this.value = newval < lowerBound ? lowerBound : newval;
        }

        if (ret) {
            updateObservers(new Integer(value));
        }
        return ret;
    }

    protected void paintComponent(Graphics g, int width, int height, boolean selected) {
        paintTitle(g, width, height, selected);
        paintHorizontalToggleArrows(g, width, height, selected);

        IFont font = getFont();

        int fontHeight = font.getHeight();
        int y = getTitleHeight() + getStyle().getPadding();

        int margin = (fontHeight / 2) * 3;
        int gaugeWidth = width - (margin * 2);
        g.drawRect(fontHeight, y, gaugeWidth, fontHeight - 1);

        int levelWidth = (int) (gaugeWidth * ((float) (value - lowerBound) / (float) (upperBound - lowerBound)));
        g.fillRect(fontHeight, y, levelWidth, fontHeight);
    }

    protected boolean canChange(byte direction) {
        return direction == Constants.DIRECTION_BACK
                ? value > lowerBound
                : value < upperBound;
    }

    public int getValueAsInt() {
        return value;
    }

    public void setValue(int intValue) {
        if (intValue != (int) Constants.NUMBER_NOT_SET)
            this.value = intValue;
    }

}
