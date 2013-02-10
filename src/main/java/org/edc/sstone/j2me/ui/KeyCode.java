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
package org.edc.sstone.j2me.ui;

import javax.microedition.lcdui.Canvas;

/**
 * @author Greg Orlowski
 */
public class KeyCode {

    /**
     * soft-left button
     */
    public static KeyCode MENU_LEFT = new KeyCode();

    /**
     * soft-right button
     */
    public static KeyCode MENU_RIGHT = new KeyCode();

    public static KeyCode LEFT = new KeyCode();
    public static KeyCode RIGHT = new KeyCode();
    public static KeyCode UP = new KeyCode();
    public static KeyCode DOWN = new KeyCode();
    public static KeyCode FIRE = new KeyCode();

    public static KeyCode NUM0 = new KeyCode();
    
    public static KeyCode ASTERISK = new KeyCode();

    // unknown keypresses
    public static KeyCode UNKNOWN = new KeyCode();

    /**
     * The keycodes that we will use to signify selection of a menu item,
     * or to select a component or component subitem (grab focus, etc). 
     */
    public static KeyCode[] SELECT_ITEM_CODES = new KeyCode[] { FIRE };

    private KeyCode() {
    }

    public static KeyCode translate(int keyCode, int gameAction) {
        switch (gameAction) {
            case Canvas.FIRE:
                return FIRE;
            case Canvas.LEFT:
                return LEFT;
            case Canvas.RIGHT:
                return RIGHT;
            case Canvas.UP:
                return UP;
            case Canvas.DOWN:
                return DOWN;
        }

        switch (keyCode) {
            case Canvas.KEY_NUM0:
                return NUM0;
            case -6: // sunwtk and many others
            case -21: // some motorola
            case -1: // some siemens
                return MENU_LEFT;
            case -7: // sunwtk and many others
            case -22:// some motorola
            case -4:// some siemens
                return MENU_RIGHT;
            case Canvas.KEY_STAR:
                return ASTERISK;
        }

        return UNKNOWN;
    }
}
