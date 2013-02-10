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

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.j2me.ui.style.theme.Theme;
import org.edc.sstone.ui.model.Dimension;
import org.edc.sstone.util.Text;

/*
 * TODO: pass in font + colors as args to an overloaded ctor.
 * For  
 */

/**
 * A component to render one or more lines of text on the screen. Similar to java.awt.TextArea or
 * javax.swing.JTextArea
 * 
 * @author Greg Orlowski
 */
public class TextArea extends EvenlyDivisibleComponent {

    private final String text;
    private Dimension dimensions = null;
    protected Vector tokens;

    protected Vector lines = new Vector(); // vector of strings
    // protected IFont font;
    protected int viewportWidth;

    /**
     * The amount of vertical margin applied above every line of text. This is dictated by
     * {@link Theme#getLineHeight()}
     * 
     * TODO: remove lineSpacing and use the style's line spacing
     */
    protected int lineSpacing;

    protected boolean addSpaceAboveFirstLine = true;

    public TextArea(String text, int contentWidth) {
        this(text, null, contentWidth, true, false, null);
    }

    public TextArea(String text, Style style, int contentWidth) {
        this(text, style, contentWidth, true, false, null);
    }

    // public TextArea(String text) {
    // this(text, Manager.getTheme().getContentWidth(), Graphics.LEFT);
    // }

    /**
     * @param text
     *            the text to display
     * @param textHorizontalJustification
     *            the horizontal text alignment within the text area (not the horizontal alignment
     *            of the text area within the viewport). This can be one of {@link Graphics#LEFT},
     *            {@link Graphics#HCENTER}, or {@link Graphics#RIGHT}.
     */
    protected TextArea(String text, Style style, int viewportWidth,
            boolean recalculateSplitLines, boolean tokenize, Character syllableSeparator) {
        super(style);
        this.text = text;
        if (tokenize && tokens == null) {
            tokens = new Vector();
        }
        if (recalculateSplitLines) {
            recalculate(lines, tokens, viewportWidth, syllableSeparator);
        }
    }

    /**
     * Call this to reset the state if, e.g., the theme changes.
     * 
     * TODO: This is private for the time being until we figure out where and how to handle theme
     * re-initialization.
     */
    protected void recalculate(Vector lines, Vector tokens, int viewportWidth, Character syllableSeparator) {
        Style style = getStyle();

        IFont font = getFont();
        this.lineSpacing = ((int) (style.getLineHeight() * font.getHeight())) - font.getHeight();
        this.viewportWidth = viewportWidth;
        Text.splitLines(font, text, lines, tokens, viewportWidth, syllableSeparator);
        visibleRangeStart = 0;
        setVisibleRangeEnd(getVerticalSegmentCount());
        dimensions = calculateDimensions();
    }

    // TODO: we could optimize this by doing it in splitLines.
    private Dimension calculateDimensions() {
        int x, maxX = 0;
        int lineCount = getVerticalSegmentCount();

        // int padding = getStyle().getPadding();

        IFont font = getFont();
        for (int i = 0; i < lineCount; i++) {
            x = font.stringWidth(getLine(i));
            maxX = x > maxX ? x : maxX;
        }
        // + (padding * 2)
        return new Dimension(maxX, getSegmentHeight(lineCount));
    }

    protected int getVerticalSegmentCount() {
        return lines.size();
    }

    protected String getLine(int idx) {
        return (String) lines.elementAt(idx);
    }

    /**
     * 
     * @param componentAnchor
     * @return
     */
    protected int calculateXAnchor() {
        int componentWidth = dimensions.width;
        // int x = 0;

        int x = getLeftEdge(viewportWidth);
        int textHorizontalJustification = getStyle().getTextHorizontalAnchor();

        // now adjust for the text justification
        switch (textHorizontalJustification) {
            case Graphics.LEFT:
                x += 0;
                break;
            case Graphics.HCENTER:
                x += (componentWidth / 2);
                break;
            case Graphics.RIGHT:
                x += componentWidth;
                break;
        }
        return x;
    }

    protected void paintComponent(Graphics g, int width, int height, boolean selected) {
        g.setColor(getStyle().getFontColor());

        IFont font = getFont();

        int x = calculateXAnchor();
        int y = 0;
        int fontHeight = font.getHeight();
        int textAnchor = getStyle().getTextAnchor();

        if (addSpaceAboveFirstLine)
            y += lineSpacing;

        for (int i = visibleRangeStart; i < getVisibleRangeEnd(); i++) {
            String line = getLine(i);
            // if (dimensions.width <= width || font.stringWidth(line) <= width) {
            font.drawString(g, line, x, y, textAnchor);
            // } else {
            // TODO: we probably do not need to worry about truncating the right-side
            // int len = line.length();
            // while (len > 0 && font.substringWidth(line, 0, len) > width)
            // len--;
            // font.drawSubstring(g, line, 0, len, x, y, textAnchor);
            // }
            y += (lineSpacing + fontHeight);
        }
    }

    public Dimension getPreferredSize() {
        return dimensions;
    }

    /**
     * Return the number of
     */
    protected int getVisibleSegmentCount(int startIdx, int availableViewportHeight) {
        int fontHeight = getFont().getHeight();

        if (addSpaceAboveFirstLine)
            return availableViewportHeight / (lineSpacing + fontHeight);

        int lineCount = availableViewportHeight > fontHeight ? 1 : 0;
        if (lineCount == 0)
            return 0;
        availableViewportHeight -= fontHeight;
        lineCount += (availableViewportHeight / (lineSpacing + fontHeight));
        return lineCount;
    }

    protected int getSegmentHeight(int segmentCount) {
        if (addSpaceAboveFirstLine) {
            return segmentCount * (getFont().getHeight() + lineSpacing);
        }

        if (segmentCount == 0)
            return 0;
        return ((getFont().getHeight() + lineSpacing) * segmentCount) - lineSpacing;
    }

    public String toString() {
        return text;
    }

}
