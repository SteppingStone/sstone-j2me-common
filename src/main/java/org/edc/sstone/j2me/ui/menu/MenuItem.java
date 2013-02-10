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

import org.edc.sstone.event.MenuEvent;
import org.edc.sstone.event.MenuListener;
import org.edc.sstone.j2me.ui.icon.VectorIcon;

/**
 * An item to be displayed in the menubar or in a popup menu extending from the menubar.
 * 
 * @author Greg Orlowski
 */
public class MenuItem implements MenuListener {

    private MenuListener menuListener;
    public final String text;
    public VectorIcon icon;

    protected MenuItem(MenuListener menuListener, String text, VectorIcon icon) {
        this.menuListener = menuListener;
        this.text = text;
        this.icon = icon;
    }

    public static MenuItem textItem(MenuListener menuListener, String text) {
        return new MenuItem(menuListener, text, null);
    }

    public static MenuItem iconItem(MenuListener menuListener, VectorIcon icon) {
        return new MenuItem(menuListener, null, icon);
    }

    public void menuSelected(MenuEvent e) {
        menuListener.menuSelected(e);
    }

}
