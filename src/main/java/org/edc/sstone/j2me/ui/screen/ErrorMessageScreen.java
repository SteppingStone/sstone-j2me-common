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
package org.edc.sstone.j2me.ui.screen;

import org.edc.sstone.event.MenuListener;
import org.edc.sstone.il8n.MessageSource;
import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.j2me.nav.NavigationEventListener;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.component.ComponentContentPanel;
import org.edc.sstone.j2me.ui.component.TextArea;
import org.edc.sstone.j2me.ui.icon.ExitIcon;
import org.edc.sstone.j2me.ui.menu.MenuButton;
import org.edc.sstone.j2me.ui.menu.MenuItem;
import org.edc.sstone.nav.ScreenNavigation;

/**
 * @author Greg Orlowski
 */
public class ErrorMessageScreen extends ComponentScreen {

    private ComponentScreen logScreen = null;

    protected ErrorMessageScreen() {
        this(null);
    }

    protected ErrorMessageScreen(ComponentScreen logScreen) {
        super(null, new ComponentContentPanel());
        this.logScreen = logScreen;
    }

    public static ErrorMessageScreen forMessagekey(final String messageKey, byte navigationDirection) {
        return forMessagekey(messageKey, null, navigationDirection);
    }

    public static ErrorMessageScreen forMessagekey(ComponentScreen logScreen, final String messageKey,
            byte navigationDirection) {
        return forMessagekey(logScreen, messageKey, null, navigationDirection);
    }

    public static ErrorMessageScreen forMessagekey(ComponentScreen logScreen, final String messageKey,
            String[] messageArgs,
            byte navigationDirection) {
        ErrorMessageScreen errorScreen = new ErrorMessageScreen(logScreen);

        MenuListener ml = null;
        ScreenNavigation nav = Registry.getManager().getScreenNavigation();

        ml = new NavigationEventListener(nav, navigationDirection);

        MessageSource ms = Registry.getManager().getMessageSource();
        String errorMessage = (messageArgs != null && messageArgs.length > 0)
                ? ms.getString(messageKey, messageArgs)
                : ms.getString(messageKey);

        errorScreen.addComponent(new TextArea(errorMessage, Registry.getManager().getTheme().getContentWidth()));
        errorScreen.setTitle(ms.getString("application.error"));

        errorScreen.addMenuItem(MenuItem.iconItem(ml, new ExitIcon()), MenuButton.LEFT);
        errorScreen.show();

        return errorScreen;
    }

    public static ErrorMessageScreen forMessagekey(final String messageKey, String[] messageArgs,
            byte navigationDirection) {
        return forMessagekey(null, messageKey, messageArgs, navigationDirection);
    }

    protected void keyPressed(KeyCode keyCode) {
        if (logScreen != null && keyCode == KeyCode.ASTERISK) {
            logScreen.show();
        } else {
            super.keyPressed(keyCode);
        }
    }
}
