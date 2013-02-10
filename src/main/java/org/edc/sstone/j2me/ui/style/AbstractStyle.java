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
package org.edc.sstone.j2me.ui.style;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.ui.model.FixedSpacing;
import org.edc.sstone.ui.model.FontStyle;
import org.edc.sstone.ui.model.Spacing;

/**
 * @author Greg Orlowski
 */
public abstract class AbstractStyle implements Style {

    protected Spacing margin = new FixedSpacing((short) 1, (short) 0);
    protected short padding = 0;
    protected FontStyle fontStyle;

    private static final int HORIZONTAL_ANCHOR_MASK = (Graphics.LEFT | Graphics.RIGHT | Graphics.HCENTER);

    protected int componentAnchor = Graphics.TOP | Graphics.LEFT;
    protected int textAnchor = Graphics.TOP | Graphics.LEFT;

    protected float lineHeight = 1.1f;

    public IFont getFont() {
        return Registry.getManager().getFontFactory().getFont(getFontStyle());
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public Spacing getMargin() {
        return margin;
    }

    public int getPadding() {
        return padding;
    }

    public int getTextAnchor() {
        return textAnchor;
    }

    public int getComponentAnchor() {
        return componentAnchor;
    }

    public int getTextHorizontalAnchor() {
        return getTextAnchor() & HORIZONTAL_ANCHOR_MASK;
    }

    public int getTextVerticalAnchor() {
        int mask = (Graphics.TOP | Graphics.BOTTOM | Graphics.BASELINE);
        return getTextVerticalAnchor() & mask;
    }

    public int getComponentHorizontalAnchor() {
        return getComponentAnchor() & HORIZONTAL_ANCHOR_MASK;
    }

    public int getComponentVerticalAnchor() {
        int mask = (Graphics.TOP | Graphics.BOTTOM | Graphics.VCENTER);
        return getComponentVerticalAnchor() & mask;
    }

    public FontStyle getFontStyle() {
        return fontStyle;
    }

}
