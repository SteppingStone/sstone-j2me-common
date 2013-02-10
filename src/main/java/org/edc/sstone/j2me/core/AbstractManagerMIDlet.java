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

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.edc.sstone.Constants;
import org.edc.sstone.il8n.MessageSource;
import org.edc.sstone.il8n.PropertyResourceMessageSource;
import org.edc.sstone.j2me.audio.AudioPlayer;
import org.edc.sstone.j2me.device.BacklightControl;
import org.edc.sstone.j2me.font.FontFactory;
import org.edc.sstone.j2me.ui.style.theme.BlueGradientTheme;
import org.edc.sstone.j2me.ui.style.theme.Theme;
import org.edc.sstone.log.Log;
import org.edc.sstone.nav.ScreenNavigation;
import org.edc.sstone.util.StringTokenizer;

import de.enough.polish.util.DeviceInfo;

/**
 * @author Greg Orlowski
 */
public abstract class AbstractManagerMIDlet extends MIDlet implements MIDletManager {

    private DeviceScreen current;
    private AudioPlayer audioPlayer;
    private Theme theme;
    private int width = -1;
    private int height = -1;
    private MessageSource messageSource;
    protected FontFactory fontFactory;

    protected BacklightControl backlightControl;
    private ScreenNavigation nav;

    protected AbstractManagerMIDlet() {
        Registry.init(this);
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(String lang) {
        this.messageSource = new PropertyResourceMessageSource(lang);
    }

    protected void initBacklightControl() {
        int vendor = DeviceInfo.getVendor();
        String backlightControlClass = null;
        switch (vendor) {
            case DeviceInfo.VENDOR_NOKIA:
                Log.debug("(backlight init) Device is Nokia");
                backlightControlClass = "org.edc.sstone.j2me.device.nokia.NokiaDeviceControl";
                break;
        }
        if (backlightControlClass != null) {
            try {
                backlightControl = (BacklightControl) Class.forName(backlightControlClass).newInstance();
            } catch (Exception e) {
                Log.warn("Cannot instantiatie backlight control: " + backlightControlClass, e);
            }
        }

        if (backlightControl != null) {
            Object ssavDelayObj = getUserPreference(Constants.SCREENSAVER_DELAY_RECORD_ID);
            int keepAliveSeconds = ssavDelayObj != null ? ((Integer) ssavDelayObj).intValue() : 0;
            backlightControl.setKeepAliveSeconds(keepAliveSeconds);
        }
    }

    /**
     * Use this instead of {@link #getAppProperty(String)} because the latter is final, and we want
     * to be able to override how app properties are retrieved when we run in the emulator.
     * 
     * @param key
     * @return the value of {@link #getAppProperty(String)}
     */
    public String getMidletProperty(String key) {
        return getAppProperty(key);
    }

    protected void initFontFactory() {
        String fontEngineName = getMidletProperty("fontEngine");
        if (fontEngineName == null) {
            fontEngineName = "bitmap";
        }
        byte fontEngineType = "bitmap".equals(fontEngineName.trim().toLowerCase())
                ? FontFactory.FONT_IMPL_BITMAP
                : FontFactory.FONT_IMPL_SYSTEM;
        // byte[] fontSizes = new byte[] { 18, 18, 18 };
        // byte[] fontSizes = new byte[] { 15, 18, 20};
        byte[] fontSizes = new byte[] { 12, 14, 17 };

        String fontSizesProp = getMidletProperty("fontSizes");
        if (fontSizesProp != null && fontSizesProp.length() > 0) {
            StringTokenizer st = new StringTokenizer(fontSizesProp);
            for (int i = 0; st.hasMoreTokens() && i < fontSizes.length; i++) {
                String str = st.nextToken();
                try {
                    int sz = Integer.parseInt(str);
                    fontSizes[i] = (byte) sz;
                } catch (NumberFormatException e) {
                    Log.warn("Invalid property fontSizes: " + fontSizesProp, e);
                }
            }
        }

        Object fontMagnificationObj = getUserPreference(Constants.FONT_MAGNIFICATION_RECORD_ID);
        byte fontMagnification = fontMagnificationObj == null
                ? Constants.FONT_SIZE_MEDIUM
                : ((Integer) fontMagnificationObj).byteValue();

        fontFactory = new FontFactory(fontEngineType, fontSizes, fontMagnification);
    }

    protected Theme newTheme(FontFactory fontFactory) {
        String themeName = getMidletProperty("theme");
        if ("BlueGradientTheme".equals(themeName)) {
            return new BlueGradientTheme(fontFactory, getWidth(), getHeight());
        }
        return new Theme(fontFactory, getWidth(), getHeight());
    }

    protected void initDisplay() {
        Displayable displayable = null;
        try {
            for (int i = 0; i < 50; i++) {
                displayable = Display.getDisplay(this).getCurrent();
                if (displayable != null) {
                    break;
                } else {
                    Thread.sleep(100L);
                }
            }
        } catch (InterruptedException e) {
            Log.warn("interrupted: ", e);
        }

        width = displayable.getWidth();
        height = displayable.getHeight();
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void exit() {
        try {
            destroyApp(true);
            notifyDestroyed();
        } catch (MIDletStateChangeException e) {
            throw new RuntimeException("Could not destroy");
        }
    }

    protected int getWidth() {
        return width;
    }

    protected int getHeight() {
        return height;
    }

    public void setScreen(DeviceScreen deviceScreen) {

        Displayable screenCanvas = deviceScreen.getCanvas();
        Display display = Display.getDisplay(this);

        synchronized (display) {
            // Deselect the current screen.
            if (current != null) {
                try {
                    current.hideNotify();
                } catch (Throwable t) {
                    Log.warn("Unhandled exception in hideNotify() of " + current, t);
                }
            }

            // Select and set the new screen.
            current = deviceScreen;

            try {
                deviceScreen.showNotify();
            } catch (Throwable t) {
                Log.warn("Unhandled exception in showNotify() of " + current, t);
            }

            display.setCurrent(screenCanvas);

            // Issue a repaint command to the screen.
            // This fixes problems on some phones to make sure the screen
            // appears correctly. For example BlackBerry phones sometimes
            // render only a part of the screen.
            deviceScreen.repaint();

            if (Log.isDebugEnabled()) {
                Log.debug("Screen switched to " + deviceScreen);
            }
        }
    }

    public DeviceScreen getScreen() {
        return current;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    /*
     * TODO: when we select a module, we have to instantiate a new audio player with a fresh
     * ResourceProvider
     */
    public void setAudioPlayer(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    public BacklightControl getBacklightControl() {
        return backlightControl;
    }

    public synchronized void setScreenNavigation(ScreenNavigation nav) {
        this.nav = nav;
    }

    public ScreenNavigation getScreenNavigation() {
        return nav;
    }

    public FontFactory getFontFactory() {
        return fontFactory;
    }

}
