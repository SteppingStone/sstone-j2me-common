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

import org.edc.sstone.event.MenuEvent;
import org.edc.sstone.event.MenuListener;
import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.font.TextFunc;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.ui.model.Dimension;
import org.edc.sstone.util.StdLib;
import org.edc.sstone.util.Text;

/**
 * @author Greg Orlowski
 */
public class MenuItemComponent extends Component {

    private final Vector lines = new Vector();
    private Dimension dimensions;
    private MenuListener menuListener;

    public MenuItemComponent(String text, MenuListener menuListener, int viewportWidth) {
        this(null, text, menuListener, viewportWidth);
    }

    public MenuItemComponent(Style style, String text, MenuListener menuListener, int viewportWidth) {
        super(style);
        this.menuListener = menuListener;
        if(text == null || text.length() == 0) {
            text = ">>";
        }
        Text.splitLines(getFont(), text, lines, viewportWidth);
        recalculateSize();
    }

    public boolean keyPressed(KeyCode keyCode) {
        if (StdLib.arrayContainsReference(getAcceptedKeyCodes(), keyCode)) {
            menuListener.menuSelected(new MenuEvent(this));
            return true;
        }
        return false;
    }

    protected KeyCode[] getAcceptedKeyCodes() {
        return KeyCode.SELECT_ITEM_CODES;
    }

    private String getLine(int i) {
        return (String) lines.elementAt(i);
    }

    protected void paintComponent(Graphics g, int width, int height, boolean selected) {
        IFont font = getFont();
        int y = 0;
        int lineHeight = (int) (font.getHeight() * getStyle().getLineHeight());

        // Ensure that we have at least 1px of margin between lines
        if (lineHeight == font.getHeight())
            lineHeight++;

        for (int i = 0; i < lines.size(); i++) {
            font.drawString(g, getLine(i), 0, y, Graphics.LEFT | Graphics.TOP);
            y += lineHeight;
        }
    }

    protected void recalculateSize() {
        dimensions = TextFunc.calculateSize(getFont(), getStyle().getLineHeight(), lines, false);
    }

    public Dimension getPreferredSize() {
        return dimensions;
    }

    /**
     * Overrides {@link Component#acceptsInput()} to always return true
     */
    public boolean acceptsInput() {
        return true;
    }

}
