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
package org.edc.sstone.j2me.nav;

import org.edc.sstone.Constants;
import org.edc.sstone.event.MenuEvent;
import org.edc.sstone.event.MenuListener;
import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.nav.ScreenNavigation;

/**
 * @author Greg Orlowski
 */
public class NavigationEventListener implements MenuListener {

    private final ScreenNavigation nav;
    private final byte direction;

    public NavigationEventListener(ScreenNavigation nav, byte direction) {
        this.nav = nav;
        this.direction = nav != null ? direction : Constants.NAVIGATION_DIRECTION_EXIT;
    }

    public void menuSelected(MenuEvent e) {
        switch (direction) {
            case Constants.NAVIGATION_DIRECTION_NEXT:
                nav.next();
                break;
            case Constants.NAVIGATION_DIRECTION_EXIT:
                Registry.getManager().exit();
                break;
            case Constants.NAVIGATION_DIRECTION_PREVIOUS:
                nav.previous();
                break;
            case Constants.NAVIGATION_DIRECTION_UP:
                nav.up();
                break;
            case Constants.NAVIGATION_DIRECTION_RELOAD_CURR:
                nav.showCurrentScreen();
                break;
        }
    }

}
