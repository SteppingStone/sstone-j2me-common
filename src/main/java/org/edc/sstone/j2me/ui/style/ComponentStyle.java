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

import org.edc.sstone.Constants;
import org.edc.sstone.ui.model.FontStyle;
import org.edc.sstone.ui.model.Spacing;
import org.edc.sstone.util.StdLib;

/**
 * @author Greg Orlowski
 */
public class ComponentStyle extends AbstractStyle {

    protected int backgroundColor = Constants.NUMBER_NOT_SET;
    protected int fontColor = Constants.NUMBER_NOT_SET;
    protected int highlightColor = Constants.NUMBER_NOT_SET;

    /**
     * The amount of time to pause, persisted as a short of deciseconds, before an animation starts
     */
    protected short animationStartDelay = Constants.NUMBER_NOT_SET;

    /**
     * The amount of time to pause, persisted as a short of deciseconds, between each frame of an
     * animation sequence.
     */
    protected short animationPeriod = Constants.NUMBER_NOT_SET;
    private Style defaultStyle;

    public ComponentStyle(Style defaults) {
        this.defaultStyle = defaults;
    }

    /*
     * Getters + Setters
     */
    public FontStyle getFontStyle() {
        return fontStyle != null
                ? fontStyle
                : defaultStyle.getFontStyle();
    }

    public void setFontStyle(FontStyle fontStyle) {
        if (fontStyle != null) {
            if (this.fontStyle != null) {
                this.fontStyle = fontStyle.withDefaults(getFontStyle());
            } else {
                this.fontStyle = fontStyle;
            }
        }
    }

    public int getBackgroundColor() {
        return StdLib.isSet(backgroundColor) ? backgroundColor : defaultStyle.getBackgroundColor();
    }

    public int getFontColor() {
        return StdLib.isSet(fontColor) ? fontColor : defaultStyle.getFontColor();
    }

    public int getHighlightColor() {
        return StdLib.isSet(highlightColor) ? highlightColor : defaultStyle.getHighlightColor();
    }

    public long getAnimationStartDelay() {
        return StdLib.isSet(animationStartDelay)
                ? animationStartDelay * 100l
                : defaultStyle.getAnimationStartDelay();
    }

    public void setAnimationStartDelay(short val) {
        animationStartDelay = val;
    }

    public long getAnimationPeriod() {
        return StdLib.isSet(animationPeriod)
                ? animationPeriod * 100l
                : defaultStyle.getAnimationPeriod();
    }

    public void setAnimationPeriod(short val) {
        animationPeriod = val;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public void setHighlightColor(int highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setLineHeightByte(byte lineHeight) {
        this.lineHeight = ((float) lineHeight) / 10f;
    }

    /**
     * Change the underlying default style
     * 
     * @param defaultStyle
     */
    public void setDefaultStyle(Style defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public void setTextAnchor(int anchor) {
        textAnchor = anchor;
    }

    public void setAnchor(int anchor) {
        componentAnchor = anchor;
    }

    public void setMargin(Spacing margin) {
        this.margin = margin;
    }

    public void setPadding(short padding) {
        // do NOT allow negative padding
        this.padding = (short) Math.max(0, padding);
    }

}
