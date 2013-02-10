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

import org.edc.sstone.ui.model.FontStyle;
import org.edc.sstone.ui.model.Spacing;

/**
 * TODO: now that we decoupled Style from IFont, move style to sstone-common
 * 
 * @author Greg Orlowski
 */
public interface Style {

    /**
     * @return the color used to paint the background of the content area and non-selected
     *         components
     */
    public int getBackgroundColor();

    /**
     * @return the color of the default text font
     */
    public int getFontColor();

    /**
     * @return the color used to highlight selected components and otherwise draw visual attention
     *         to elements on the screen
     */
    public int getHighlightColor();

    /**
     * @return the style to use for text
     */
    public FontStyle getFontStyle();

    /**
     * @return the factor by which the font height should be multiplied to calculate the height of a
     *         line of text. This should be >= 1.0f. E.g., if the font size is 10px and the line
     *         height factor is 1.1f, a line of text will be 11px, resulting in 1px of padding above
     *         characters.
     */
    public float getLineHeight();

    /**
     * Represents margin around components
     */
    public Spacing getMargin();

    /**
     * Represents vertical spacing inside the boundaries of a component
     * 
     * TODO: use padding more consistently
     */
    public int getPadding();

    /**
     * @return the integer anchor of text within the component. This uses the same integer anchor
     *         system as javax.microedition.lcdui.Graphics
     */
    public int getTextAnchor();

    /**
     * @return the integer anchor of the component within its container. This uses the same integer
     *         anchor system as javax.microedition.lcdui.Graphics
     */
    public int getComponentAnchor();

    public int getTextHorizontalAnchor();

    public int getTextVerticalAnchor();

    public int getComponentHorizontalAnchor();

    public int getComponentVerticalAnchor();

    public long getAnimationStartDelay();

    public long getAnimationPeriod();
}
