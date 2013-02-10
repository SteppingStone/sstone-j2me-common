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

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.edc.sstone.Constants;
import org.edc.sstone.j2me.core.DeviceScreen;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.icon.ArrowHeadIcon;
import org.edc.sstone.j2me.ui.icon.VectorIcon;
import org.edc.sstone.j2me.ui.style.theme.Theme;

/**
 * @author Greg Orlowski
 */
public class PopupMenuButton extends MenuButton {

    private Vector menuItems = new Vector();
    private int selectedItemIdx = 0;
    private boolean visible = false;
    protected VectorIcon showPopupIcon;

    public PopupMenuButton(DeviceScreen screen, byte pos) {
        super(screen, pos);
        showPopupIcon = new ArrowHeadIcon(Canvas.UP);
        menuPosition = pos;
    }

    /**
     * @return the action that should occur when you click the popup menu button when the menu is
     *         visible (either blur or select the item).
     */
    protected byte menuButtonAction() {
        return Constants.ACTION_SELECT;
    }

    public void addMenuItem(MenuItem item) {
        menuItems.addElement(item);
    }

    public void paint(Graphics g, Theme theme, int screenWidth) {
        if (menuItems.size() == 0)
            return;

        if (menuItems.size() == 1) {
            paintButton(g, getMenuItem(0), theme, screenWidth);
        } else {
            paintIcon(g, showPopupIcon, theme, screenWidth);
            if (visible) {
                paintPopupItems(g, theme);
            }
        }
    }

    protected void paintPopupItems(Graphics g, Theme theme) {
        // Note that the bottom item is the item with idx==0. Since the menu
        // goes up and starts at the bottom, the bottom items are most important
        // and the top should be least important
        int menuItemHeight = theme.getPopupMenuItemHeight();
        int itemTop = theme.getMenubarTop() - menuItemHeight;
        int menuItemWidth = theme.getPopupMenuItemWidth();

        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem menuItem = getMenuItem(i);

            int bgColor = (i == selectedItemIdx)
                    ? theme.getPopupMenuHighlightColor()
                    : theme.getPopupMenuBackgroundColor();

            // TODO: (post v1.0) support center or right popups...
            paintMenuItem(g, menuItem,
                    bgColor, 0, itemTop,
                    menuItemWidth, menuItemHeight);

            itemTop -= menuItemHeight;
        }
    }

    protected void paintMenuItem(Graphics g, MenuItem menuItem,
            int bgColor, int x, int y,
            int width, int height) {

        int iconHeight = (height / 6) * 5;
        iconHeight = iconHeight % 2 == 0 ? iconHeight - 1 : iconHeight;

        int iconWidth = iconHeight;
        int iconX = (width / 2) - (iconWidth / 2);
        int iconY = (height / 2) - (iconHeight / 2);

        g.setColor(bgColor);
        g.fillRect(x, y, width, height);

        // TODO: support text icons
        menuItem.icon.paint(g, x + iconX, y + iconY, iconWidth, iconHeight);
    }

    // private int getHeight() {
    // int itemHeight = Registry.getManager().getTheme().getPopupMenuItemHeight();
    // return itemHeight * menuItems.size();
    // }

    /**
     * @param keyCode
     *            the keyCode that was pressed
     * @return true if the keycode pressed maps to the position of the {@link PopupMenuButton} else
     *         false
     */
    protected boolean isPositionCode(KeyCode keyCode) {
        return ((keyCode == KeyCode.MENU_LEFT && menuPosition == MenuButton.LEFT)
        || (keyCode == KeyCode.MENU_RIGHT && menuPosition == MenuButton.RIGHT));
    }

    public boolean keyPressed(KeyCode keyCode) {

        // if there is only 1 item, always forward the event to the item.
        if (isPositionCode(keyCode) && menuItems.size() == 1) {
            return sendMenuEvent(getMenuItem(0));
        }

        if (!isVisible()) {
            setVisible(true);
            return true;
        }

        /*
         * If we are here then the popup menu is visible
         */

        if (keyCode == KeyCode.UP) {
            if (selectedItemIdx < menuItems.size() - 1) {
                selectedItemIdx++;
                repaint();
            }
        } else if (keyCode == KeyCode.DOWN) {
            if (selectedItemIdx > 0) {
                selectedItemIdx--;
                repaint();
            } else {
                setVisible(false);
            }
        } else if (keyCode == KeyCode.FIRE
                || (isPositionCode(keyCode) && menuButtonAction() == Constants.ACTION_SELECT)) {
            return sendMenuEvent(getMenuItem(selectedItemIdx));
        } else {
            // any other key will always blur the menu

            // Log.debug("pos? " + menuPosition);
            // Log.debug("keyCode is left? " + (keyCode == KeyCode.LEFT));
            // Log.debug("isPositionCode: " + isPositionCode(keyCode));

            selectedItemIdx = 0;
            setVisible(false);
        }
        return true;
    }

    private MenuItem getMenuItem(int idx) {
        return (MenuItem) menuItems.elementAt(idx);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        repaint();
    }

    protected void repaint() {
        if (screen.isShown()) {
            screen.repaint();
        }
    }

}
