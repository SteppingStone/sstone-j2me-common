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
package org.edc.sstone.j2me.ui.menu;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.event.MenuEvent;
import org.edc.sstone.j2me.core.DeviceScreen;
import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.icon.VectorIcon;
import org.edc.sstone.j2me.ui.style.theme.Theme;

/**
 * @author Greg Orlowski
 */
public abstract class MenuButton {

    public static final byte LEFT = 0;
    public static final byte CENTER = 1;
    public static final byte RIGHT = 2;

    protected final DeviceScreen screen;
    private boolean enabled = true;
    protected byte menuPosition;

    public MenuButton(DeviceScreen screen, byte pos) {
        this.screen = screen;
        this.menuPosition = pos;
    }

    /**
     * Paint the button at the top-left corner
     * 
     * @param g
     *            the Graphics object
     * @param x
     *            the left edge of the button
     * @param y
     *            the top of the button the width of the button
     * @param width
     * @param height
     *            the height of the button
     */
    public abstract void paint(Graphics g, Theme theme, int screenWidth);

    public abstract boolean keyPressed(KeyCode keyCode);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected void paintButton(Graphics g, MenuItem menuItem, Theme theme, int screenWidth) {
        if (menuItem.icon != null) {
            paintIcon(g, menuItem.icon, theme, screenWidth);
        } else {
            paintText(g, menuItem.text, theme, screenWidth);
        }
    }

    protected void paintIcon(Graphics g, VectorIcon icon, Theme theme, int screenWidth) {
        int height = theme.getMenubarIconHeight();
        int width = height;

        int y = theme.getMenubarIconTop();
        int x = theme.getMenubarIconLeftEdge(menuPosition);
        g.setColor(isEnabled() ? theme.getMenubarIconColor() : theme.getMenuItemDisabledColor());
        icon.paint(g, x, y, width, height);
    }

    public void paintText(Graphics g, String text, Theme theme, int screenWidth) {
        int oldColor = g.getColor();
        g.setColor(theme.getMenubarFontColor());

        IFont tr = theme.getMenubarFont();

        int x = 0;
        int y = theme.getMenubarTextTop();
        int anchor = Graphics.TOP;

        switch (menuPosition) {
            case MenuButton.LEFT:
                anchor |= Graphics.LEFT;
                x = theme.getMenubarTextMargin();
                break;
            case MenuButton.CENTER:
                anchor |= Graphics.HCENTER;
                x = screenWidth / 2;
                break;
            case MenuButton.RIGHT:
                anchor |= Graphics.RIGHT;
                x = screenWidth - theme.getMenubarTextMargin();
                break;
        }

        tr.drawString(g, text, x, y, anchor);
        g.setColor(oldColor);
    }

    protected boolean sendMenuEvent(MenuItem menuItem) {
        menuItem.menuSelected(new MenuEvent(this));
        return true;
    }

}
