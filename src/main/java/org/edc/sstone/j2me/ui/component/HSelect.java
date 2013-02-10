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
public class HSelect extends AbstractHorizontalSelectComponent {

    private final Option[] options;
    private int optionIndex = 0;

    public HSelect(Style style, String title, Option[] options) {
        super(style, title);
        this.options = options;
    }

    public boolean changeValue(byte direction) {
        if (canChange(direction)) {
            optionIndex += direction;
            updateObservers(new Integer(options[optionIndex].value));
            return true;
        }
        return false;
    }

    protected void paintComponent(Graphics g, int width, int height, boolean selected) {
        paintTitle(g, width, height, selected);
        paintHorizontalToggleArrows(g, width, height, selected);

        Style style = getStyle();
        IFont font = getFont();

        int fontHeight = font.getHeight();
        int y = getTitleHeight() + getStyle().getPadding();
        int x = fontHeight;

        int margin = (fontHeight / 2) * 3;
        int boxWidth = width - (margin * 2);

        // g.setColor(selected ? style.getHighlightColor() : style.getBackgroundColor());
        // g.setColor(selected ? style.getFontColor() : style.getBackgroundColor());
        g.setColor(selected ? style.getFontColor() : style.getHighlightColor());
        g.fillRect(x, y, boxWidth, fontHeight);

        int midpoint = x + (boxWidth / 2);
        g.setColor(selected ? style.getBackgroundColor() : style.getFontColor());
        font.drawString(g, getValue().displayValue, midpoint, y, Graphics.HCENTER | Graphics.TOP);
    }

    public Option getValue() {
        return options[optionIndex];
    }

    protected boolean canChange(byte direction) {
        return direction == Constants.DIRECTION_BACK
                ? optionIndex > 0
                : optionIndex < options.length - 1;
    }

    public static HSelect forNumericRange(Style style, String title, int min, int max, int step) {
        return new HSelect(style, title, buildOptions(min, max, step));
    }

    private static Option[] buildOptions(int min, int max, int step) {
        int numSteps = ((max - min) / step) + 1;
        Option[] ret = new Option[numSteps];
        for (int i = 0; i < numSteps; i++) {
            ret[i] = new Option(i * step);
        }
        return ret;
    }

    public void setValue(int value) {
        // If value is Constants.NUMBER_NOT_SET, do nothing
        if (value != Constants.NUMBER_NOT_SET) {
            for (int i = 0; i < options.length; i++) {
                if (value == options[i].value) {
                    optionIndex = i;
                    break;
                }
            }
        }
    }
}
