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

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.edc.sstone.Constants;
import org.edc.sstone.event.Observer;
import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.ui.model.Dimension;
import org.edc.sstone.util.Text;

/**
 * @author Greg Orlowski
 */
public abstract class AbstractHorizontalSelectComponent extends Component {

    private Dimension dimension;
    public final String[] title;
    // protected ValueSourceObserver valueSourceObserver;
    protected Vector observers;

    /**
     * @param direction
     *            Should be one of {@link Constants#DIRECTION_BACK} or
     *            {@link Constants#DIRECTION_FORWARD}
     * @return true if the value was changed else false
     */
    protected abstract boolean changeValue(byte direction);

    /**
     * @param direction
     *            Should be one of {@link Constants#DIRECTION_BACK} or
     *            {@link Constants#DIRECTION_FORWARD}
     * @return true if the value can be changed else false
     */
    protected abstract boolean canChange(byte direction);

    protected AbstractHorizontalSelectComponent(Style style, String title) {
        super(style);
        this.title = splitTitle(title);
        recalculateGeometry();
    }

    protected String[] splitTitle(String title) {
        int viewportWidth = Registry.getManager().getTheme().getContentWidth();
        Vector lines = new Vector();
        Text.splitLines(getFont(), title, lines, viewportWidth);

        String[] ret = new String[lines.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = lines.elementAt(i).toString();

        return ret;
    }

    public boolean keyPressed(KeyCode keyCode) {
        boolean changed = false;
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT) {
            changed = changeValue((keyCode == KeyCode.LEFT) ? Constants.DIRECTION_BACK : Constants.DIRECTION_FORWARD);
            if (changed)
                repaint();
        }
        return changed;
    }

    protected void paintTitle(Graphics g, int width, int height, boolean selected) {
        int y = 0;
        for (int i = 0; i < title.length; i++) {
            getFont().drawString(g, title[i] + ":", 0, y, Graphics.LEFT | Graphics.TOP);
            y += (int) (getFont().getHeight() * 1.1f);
        }
    }

    protected void paintHorizontalToggleArrows(Graphics g, int width, int height, boolean selected) {
        if (selected) {
            IFont font = getFont();

            // padding WITHIN the component (above the arrows)
            int y = (getTitleHeight() + Math.min(getStyle().getPadding(), 2));

            int arrowWidth = font.getHeight();
            while (arrowWidth % 2 == 0)
                arrowWidth--;

            g.setColor(getStyle().getFontColor());

            if (canChange(Constants.DIRECTION_BACK))
                paintArrow(g, Canvas.LEFT, 0, y, arrowWidth);

            if (canChange(Constants.DIRECTION_FORWARD))
                paintArrow(g, Canvas.RIGHT, width - arrowWidth, y, arrowWidth);
        }
    }
    
    protected int getTitleHeight() {
        return (int) (1.1f * title.length * getFont().getHeight());
    }

    protected void paintArrow(Graphics g, int direction, int x, int y, int arrowWidth) {
        int ax = (direction == Canvas.RIGHT) ? x : x + (arrowWidth / 2);
        int bx = (direction == Canvas.LEFT) ? x : x + (arrowWidth / 2);
        int cx = ax;

        int ay = y;
        int by = y + (arrowWidth / 2);
        int cy = y + arrowWidth - 1;

        g.fillTriangle(ax, ay,
                bx, by,
                cx, cy);
    }

    public Dimension getPreferredSize() {
        return dimension;
    }

    public boolean acceptsInput() {
        return true;
    }

    public void repaint() {
        super.repaint();
        // DeviceScreen screen = getScreen();
        // if (screen.isShown())
        // screen.repaint();
    }

    protected void recalculateGeometry() {
        IFont font = getFont();

        /*
         * Use padding both between parts of the component and around the entire thing (within its
         * border). Add a little extra padding to the bottom to compensate for the fact that
         * top-anchored text has a couple extra pixels above the top of capital letters
         */
        int height = getStyle().getPadding()
                + (int) (font.getHeight() * 1.3f)
                + getTitleHeight();
        int width = font.stringWidth(title[0]);
        for (int i = 1; i < title.length; i++) {
            width = Math.max(width, font.stringWidth(title[i]));
        }

        this.dimension = new Dimension(width, height);
    }

    public void addObserver(Observer observer) {
        if (observers == null) {
            observers = new Vector();
        }
        observers.addElement(observer);
    }

    protected void updateObservers(Object value) {
        if (observers != null) {
            for (int i = 0; i < observers.size(); i++) {
                ((Observer) observers.elementAt(i)).update(value);
            }
        }
    }
}
