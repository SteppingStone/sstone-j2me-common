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
package org.edc.sstone.j2me.core;

import org.edc.sstone.il8n.MessageSource;
import org.edc.sstone.j2me.audio.AudioPlayer;
import org.edc.sstone.j2me.device.BacklightControl;
import org.edc.sstone.j2me.font.FontFactory;
import org.edc.sstone.j2me.ui.style.theme.Theme;
import org.edc.sstone.nav.ScreenNavigation;
import org.edc.sstone.res.ResourceProvider;
import org.edc.sstone.store.ValueSource;

/**
 * @author Greg Orlowski
 */
public interface MIDletManager {

    public Theme getTheme();

    public void setTheme(Theme theme);

    public void exit();

    public void setScreen(DeviceScreen deviceScreen);

    public BacklightControl getBacklightControl();

    /**
     * Returns the currently selected screen. If no screen is set, or a DeviceScreen is displayed,
     * this will return <code>null</code>.
     * <p>
     * The application can call <code>toString</code> on the returned screen to get its name for
     * logging purposes.
     * 
     * @return The currently displayed DeviceScreen or <code>null</code> if none is set.
     */
    public DeviceScreen getScreen();

    public MessageSource getMessageSource();
    
    public void setMessageSource(String lang);

    /**
     * @return an initialized
     */
    public AudioPlayer getAudioPlayer();

    public void setAudioPlayer(AudioPlayer audioPlayer);

    public ResourceProvider getResourceProvider();

    public void setResourceProvider(ResourceProvider resourceProvider);

    public String getMidletProperty(String key);

    public void handleException(Throwable e);

    public void setScreenNavigation(ScreenNavigation nav);

    public ScreenNavigation getScreenNavigation();

    public Object getUserPreference(int recordId);

    public ValueSource getUserPreferences();

    public FontFactory getFontFactory();

    public void showMainMenu();
}