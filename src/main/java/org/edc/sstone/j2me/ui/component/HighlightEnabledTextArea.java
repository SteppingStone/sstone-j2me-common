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

import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.ui.scroll.ScrollDirection;
import org.edc.sstone.j2me.ui.scroll.ScrollHandler;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.util.Text;

/**
 * @author Greg Orlowski
 */
public abstract class HighlightEnabledTextArea extends TextArea implements AnimatedComponent {

    protected int tokenIdx = -1;

    protected HighlightEnabledTextArea(String text, Style style, int viewportWidth, Character syllableSeparator) {
        super(text, style, viewportWidth, true, true, syllableSeparator);
    }

    public String getHighlightedTokenString() {
        return getHighlightedToken().text;
    }

    protected Text.Token getHighlightedToken() {
        return ishighlightingEnabled() ? (Text.Token) tokens.elementAt(tokenIdx) : null;
    }

    protected void paintComponent(Graphics g, int width, int height, boolean selected) {
        g.setColor(getStyle().getFontColor());

        IFont font = getFont();

        int y = 0, x = calculateXAnchor();
        int fontHeight = font.getHeight();

        if (addSpaceAboveFirstLine)
            y += lineSpacing;

        Text.Token highlightedToken = getHighlightedToken();
        int textAnchor = getStyle().getTextAnchor();

        for (int i = visibleRangeStart; i < getVisibleRangeEnd(); i++) {
            String line = getLine(i);

            if (ishighlightingEnabled() && highlightedToken.lineIdx == i) {
                int color = g.getColor();
                //g.setColor(0xF6F27B);
                g.setColor(getStyle().getHighlightColor());
                g.fillRect(x + font.substringWidth(line, 0, highlightedToken.startPos) - 1,
                        y,
                        highlightedToken.tokenWidth + 1,
                        font.getHeight());
                g.setColor(color);
            }

            font.drawString(g, line, x, y, textAnchor);
            y += (lineSpacing + fontHeight);
        }
    }

    private boolean ishighlightingEnabled() {
        return tokenIdx >= 0;
    }

    public boolean hasMoreFrames() {
        return hasMoreTokens();
    }

    public void advanceFrame(ScrollHandler scrollHandler) {
        advanceTokenPointer();
        if (isSelfScrollNeeded() && scrollHandler.canScroll(ScrollDirection.DOWN)) {
            scrollHandler.scroll(ScrollDirection.DOWN);
        }
        repaint();
    }

    protected void advanceTokenPointer() {
        tokenIdx++;
    }

    protected boolean hasMoreTokens() {
        return tokenIdx < (tokens.size() - 1);
    }

    protected boolean isSelfScrollNeeded() {
        return getHighlightedToken().lineIdx > (getVisibleRangeEnd() - 1);
    }

    public void reset() {
        tokenIdx = -1;
    }

}
