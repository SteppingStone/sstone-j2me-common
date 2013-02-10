/*
 * This code was originally part of the J4ME project:
 *
 * https://code.google.com/p/j4me/
 *
 * Copyright 2007 J4ME
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.  See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.edc.sstone.j2me.core;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.device.BacklightControl;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.menu.MenuButton;
import org.edc.sstone.j2me.ui.menu.MenuItem;
import org.edc.sstone.j2me.ui.menu.MenuItemButton;
import org.edc.sstone.j2me.ui.menu.PopupMenuButton;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.j2me.ui.style.theme.Theme;
import org.edc.sstone.log.Log;
import org.edc.sstone.util.StdLib;

import de.enough.polish.util.DeviceInfo;

/**
 * The <code>DeviceScreen</code> class is a base class for any screen that needs complete control
 * over how it is painted. It is based on and similar to the MIDP <code>Canvas</code> class.
 * 
 * @see javax.microedition.lcdui.Canvas
 */
public abstract class DeviceScreen {

    /**
     * The actual <code>Canvas</code> object that controls the device's screen. This object wraps
     * it.
     */
    private final CanvasWrapper slave;

    protected static boolean useKeyRepeater = !DeviceInfo.sendsMultipleKeyPressesWhenHoldingDownKeys();

    protected Timer keyRepeater = useKeyRepeater ? new Timer() : null;

    /**
     * What is written as a title bar for this canvas. When this is <code>null</code> no title bar
     * will be written. To show the header without any text set this to the empty string "".
     */
    private String title;

    /**
     * The 3 menu buttons in the menubar, indexed from left-to-right
     */
    protected MenuButton[] menuButtons = new MenuButton[3];

    /**
     * Implicitly called by derived classes to setup a new J4ME canvas.
     */
    public DeviceScreen() {
        slave = new CanvasWrapper(this);
    }

    /**
     * Returns the LCDUI <code>Canvas</code> wrapped by this screen. This is required for some APIs.
     * 
     * @return The <code>javax.microedition.lcdui.Canvas</code> wrapped by this screen.
     */
    protected Canvas getCanvas() {
        return slave;
    }

    /**
     * Makes this object take over the device's screen.
     * <p>
     * The previous screen will have its <code>hideNotify</code> method called. Then this screen's
     * <code>showNotify</code> method will be invoked followed by the <code>paint</code> method.
     */
    public void show() {
        // Set the wrapped canvas as the current screen.
        getManager().setScreen(this);
    }

    /**
     * Checks if this screen is actually visible on the display. In order for a screen to be
     * visible, all of the following must be true: the MIDlet must be running in the foreground, the
     * screen must be the current one, and the screen must not be obscured by a system screen.
     * 
     * @return <code>true</code> if this screen is currently visible; <code>false</code> otherwise.
     */
    public boolean isShown() {
        return getManager().getScreen() == this;
    }

    protected MIDletManager getManager() {
        return Registry.getManager();
    }

    /**
     * The implementation calls <code>showNotify()</code> immediately prior to this
     * <code>Canvas</code> being made visible on the display. <code>Canvas</code> subclasses may
     * override this method to perform tasks before being shown, such as setting up animations,
     * starting timers, etc. The default implementation of this method in class <code>Canvas</code>
     * is empty.
     */
    public void showNotify() {
    }

    /**
     * The implementation calls <code>hideNotify()</code> shortly after the <code>Canvas</code> has
     * been removed from the display. <code>Canvas</code> subclasses may override this method in
     * order to pause animations, revoke timers, etc.
     */
    public void hideNotify() {
        slave.stopKeyRepeater();
        if (keyRepeater != null) {
            /*
             * An IllegalStateException can be thrown in J2ME's Timer impl if you cancel an
             * already-canceled timer. Catch + ignore this.
             */
            try {
                keyRepeater.cancel();
            } catch (Exception ignore) {
            }
        }
    }

    /*
     * This will be called when we switch screens. TODO: unlike with a canvas, our hideNotify will
     * not be called unless we actually call setScreen. So maybe we can just do what we need
     */
    // public void deallocate() {
    // }

    /**
     * Gets the title of this screen. If this returns <code>null</code> the screen has no title.
     * 
     * @return The title of this screen.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this screen. The default is <code>null</code> meaning no title.
     * <p>
     * For the title to be visible full screen mode must be off. This can be done with the
     * <code>setFullScreenMode</code> method.
     * 
     * @param title
     *            is the new title for the screen.
     */
    public void setTitle(String title) {
        this.title = title;

        // Notify the slave screen.
        slave.setTitle(title);

        if (slave.isShown())
            slave.repaint();
    }

    /**
     * Returns the title of this screen.
     * 
     * @return The title of this screen. If no title is set this returns the empty string "".
     */
    public String toString() {
        if (title == null) {
            // NOTE:
            // getClass().getName() will not return a meaningful string in code-obfuscated
            // environments.
            return getClass().getName();
        }
        return title;
    }

    /**
     * Add a menu item to the menubar.
     * 
     * @param menuItem
     *            the MenuItem to add
     * @param pos
     *            the position where we want the item added (one of {@link MenuButton#LEFT},
     *            {@link MenuButton#CENTER}, {@link MenuButton#RIGHT})
     */
    public void addMenuItem(MenuItem menuItem, byte pos) {
        MenuButton button = menuButtons[pos];
        if (button == null) {
            menuButtons[pos] = new MenuItemButton(this, menuItem, pos);
        } else if (button instanceof PopupMenuButton) {
            ((PopupMenuButton) button).addMenuItem(menuItem);
        } else if (button instanceof MenuItemButton) {
            PopupMenuButton pmb = newPopupMenuButton(pos);
            pmb.addMenuItem(((MenuItemButton) button).menuItem);
            pmb.addMenuItem(menuItem);
            menuButtons[pos] = pmb;
        }
    }

    /**
     * @return a new {@link PopupMenuButton}. Override this to return a subclass of PopupMenuButton
     *         if you want to alter, e.g., the keypress behaviors of PopupMenuButton.
     */
    protected PopupMenuButton newPopupMenuButton(byte pos) {
        return new PopupMenuButton(this, pos);
    }

    public MenuButton getMenuButton(byte pos) {
        return menuButtons[pos];
    }

    /**
     * @return <code>true</code>
     */
    public boolean hasMenubar() {
        return true;
    }

    /**
     * Returns the width of the usable portion of this canvas. The usable portion excludes anything
     * on the sides of the screen such as scroll bars.
     * 
     * TODO: this implementation is busted -- we need to account for scrollbar width
     * 
     * @return The number of pixels wide the usable portion of the canvas is.
     */
    public int getWidth() {
        return slave.getWidth();
    }

    /**
     * Returns the height of the usuable portion of this canvas. The usable portion excludes the
     * title area and menu bar unless this canvas has been set to full screen mode.
     * 
     * @return The number of pixels high the usable portion of the canvas is.
     */
    public int getHeight() {

        // Get the height of the entire canvas.
        int height = getScreenHeight();

        // Remove the height of the title bar.
        if (hasTitleBar())
            height -= getTheme().getTitleBarHeight();

        // Remove the height of the menu bar.
        if (hasMenubar())
            height -= getTheme().getMenubarHeight();

        return height;
    }

    /**
     * Gets the width of the entire screen in pixels.
     * <p>
     * <i>Platform bug note.</i> Motorola and early Nokia phones return the incorrect size until
     * after the first screen has actually been displayed. So, for example, calling this from a
     * constructor before any screen has been displayed will give incorrect data. The workaround is
     * to put up another screen first, such as a splash screen.
     * 
     * @return The number of pixels wide the entire screen is.
     */
    public int getScreenWidth() {
        return slave.getWidth();
    }

    /**
     * Gets the height of the entire screen in pixels. This includes the title area at the top of
     * the screen and menu bar at the bottom. Use <code>getHeight</code> to get the actual usable
     * area of the canvas.
     * <p>
     * <i>Platform bug note.</i> Motorola and early Nokia phones return the incorrect size until
     * after the first screen has actually been displayed. So, for example, calling this from a
     * constructor before any screen has been displayed will give incorrect data. The workaround is
     * to put up another screen first, such as a splash screen.
     * 
     * @return The number of pixels high the entire screen is.
     */
    public int getScreenHeight() {
        return slave.getHeight();
    }

    /**
     * Called when a key is pressed.
     * 
     * @param keyCode
     *            is the KeyCode of the key that was pressed.
     */
    protected void keyPressed(KeyCode keyCode) {
        BacklightControl backlightControl = Registry.getManager().getBacklightControl();
        if (backlightControl != null) {
            backlightControl.stayLit();
        }
    }

    /**
     * Called when a key is held down after it is pressed.
     * 
     * @param keyCode
     *            is the KeyCode of the key that was pressed.
     */
    protected void keyRepeated(KeyCode kc) {
    }

    /**
     * Requests a repaint for the entire <code>Canvas</code>. The effect is identical to
     * <code>repaint(0, 0, getWidth(), getHeight());</code>.
     */
    public void repaint() {
        // Make sure the wrapper is in full-screen mode.
        // There is a bug on some implementations that turns the screen
        // off full-screen mode. This can be seen when going to a
        // javax.microedition.lcdui.TextBox screen and back to this one.
        slave.setFullScreenMode(true);

        // Do the repaint.
        slave.repaint();
    }

    public void repaint(int x, int y, int width, int height) {
        if (hasTitleBar()) {
            // Offset the user's y by the height of the title bar.
            int titleHeight = getTheme().getTitleBarHeight();
            y += titleHeight;
        }
        slave.repaint(x, y, width, height);
    }

    protected boolean hasTitleBar() {
        return true;
    }

    /**
     * Forces any pending repaint requests to be serviced immediately. This method blocks until the
     * pending requests have been serviced. If there are no pending repaints, or if this canvas is
     * not visible on the display, this call does nothing and returns immediately.
     */
    public void serviceRepaints() {
        slave.serviceRepaints();
    }

    /**
     * Paints the background of the main section of the screen. This includes everything except for
     * the title bar at the top and menu bar at the bottom. However, if this canvas is in full
     * screen mode, then this method paints the entire screen.
     * <p>
     * After this method is called, the <code>paintCanvas</code> method will be.
     * <p>
     * Override this method to change the background for just this screen. Override
     * <code>Theme.paintBackground</code> to change the background for the entire application.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     * @see #paint(Graphics)
     */
    protected void paintBackground(Graphics g) {
        int color = getStyle().getBackgroundColor();

        int x = g.getClipX();
        int y = g.getClipY();
        int w = g.getClipWidth();
        int h = g.getClipHeight();

        // Clear the canvas.
        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    public Style getStyle() {
        return getTheme();
    }

    protected Theme getTheme() {
        return getManager().getTheme();
    }

    /**
     * Paints the main section of the screen. This includes everything except for the title bar at
     * the top and menu bar at the bottom. However, if this canvas is in full screen mode, then this
     * method paints the entire screen.
     * <p>
     * Before this method is called, the <code>paintBackground</code> method will be. Any painting
     * done here will go over the background.
     * <p>
     * Override this method to paint the main area of the screen.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     * @see #paintBackground(Graphics)
     */
    protected abstract void paintContentArea(Graphics g);

    /**
     * Paints the title bar of the canvas. This method is called only when the title has been set
     * through <code>setTitle</code> and the canvas is not in full screen mode.
     * <p>
     * Override this method to change the appearance of the title bar for just this canvas. To
     * change them for the entire application, override <code>Theme.paintTitleBar</code>.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     * @param title
     *            is the text for the title bar as defined by the canvas class.
     * @param width
     *            is the width of the title bar in pixels.
     * @param height
     *            is the height of the title bar in pixels.
     */
    protected void paintTitleBar(Graphics g) {
        getTheme().paintTitleBar(g, getTitle());
    }

    /**
     * Paints the menu bar at the bottom of the canvas. This method is not called if the canvas is
     * in full screen mode.
     * <p>
     * Override this method to change the appearance or functionality of the menu for just this
     * screen. To change them for the entire application, override <code>Theme.paintMenubar</code>.
     * Be careful not to write strings that are too long and will not fit on the menu bar.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     */
    protected void paintMenubar(Graphics g) {
        getTheme().paintMenubar(g, menuButtons);
    }

    /**
     * Returns if the clip area of <code>g</code> intersects the given rectangle.
     * 
     * @param g
     *            is the current <code>Graphics</code> object for a <code>paint</code> operation.
     * @param x
     *            is the pixel at the left edge of the rectangle.
     * @param y
     *            is the pixel at the top edge of the rectangle.
     * @param w
     *            is width of the rectangle in pixels.
     * @param h
     *            is height of the rectangle in pixels.
     * @return <code>true</code> if the rectangle is to be painted by <code>g</code>;
     *         <code>false</code> otherwise.
     */
    public static boolean intersects(Graphics g, int x, int y, int w, int h) {
        // Get the graphic's clip dimensions.
        int gx = g.getClipX();
        int gy = g.getClipY();
        int gw = g.getClipWidth();
        int gh = g.getClipHeight();

        // Make the width/height into the right/bottom.
        gw += gx;
        gh += gy;
        w += x;
        h += y;

        // Check for intersections.
        // (overflow || intersect)
        boolean intersects =
                (w < x || w > gx) &&
                        (h < y || h > gy) &&
                        (gw < gx || gw > x) &&
                        (gh < gy || gh > y);
        return intersects;
    }

    /**
     * @return an array of the keycodes for which repeat events will be sent.
     */
    protected KeyCode[] getRepeatableKeys() {
        return new KeyCode[] { KeyCode.UP, KeyCode.DOWN };
    }

    /**
     * Wraps the LCDUI's <code>Canvas</code> class. It masks differences between platforms. It is
     * used as the actual <code>Screen</code> for all J4ME screens.
     */
    private static final class CanvasWrapper
            extends javax.microedition.lcdui.Canvas {

        private TimerTask timerTask;

        // implements CommandListener {

        /**
         * The screen that uses this object for screen operations.
         * <p>
         * This should only be <code>null</code> when using a dummy screen to get the dimensions.
         * For more information see the <code>sizeChanged</code> method's comments for more
         * information.
         */
        private final DeviceScreen master;

        /**
         * Constructs a wrapper for a <code>Canvas</code>.
         * 
         * @param master
         *            is the J4ME screen that uses this object.
         */
        public CanvasWrapper(DeviceScreen master) {
            this.master = master;
            setFullScreenMode(true);
        }

        /**
         * Called when the user presses any key.
         * 
         * @param key
         *            is code of the key that was pressed.
         */
        protected void keyPressed(int key) {
            stopKeyRepeater();

            int action = 0;
            try {
                action = getGameAction(key);
            } catch (Exception e) {
                // Some phones throw an exception for unsupported keys.
                // For example the Sony Ericsson K700.
            }

            final KeyCode kc = KeyCode.translate(key, action);
            master.keyPressed(kc);

            if (useKeyRepeater) {
                // Log.debug("Using key repeater...");
                KeyCode[] repeatableKeys = master.getRepeatableKeys();
                if (StdLib.arrayContainsReference(repeatableKeys, kc)) {
                    synchronized (CanvasWrapper.this) {
                        stopKeyRepeater();
                        timerTask = new TimerTask() {
                            public void run() {
                                master.keyRepeated(kc);
                            }
                        };
                        master.keyRepeater.schedule(timerTask, 200l, 100l);
                    }
                }
            }
        }

        protected void keyReleased(int keyCode) {
            stopKeyRepeater();
        }

        protected synchronized void stopKeyRepeater() {
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
        }

        /**
         * Paints the screen. If not in full screen mode, this includes the title bar and menu bar.
         * <p>
         * Typically derviced classes will only override the <code>paintCanvas</code> method.
         * 
         * @param g
         *            is the <code>Graphics</code> object to paint with.
         * @see #paintTitleBar(Graphics, String, int, int, int, int)
         * @see #paintBackground(Graphics, int, int, int, int)
         * @see #paintCanvas(Graphics, int, int, int, int)
         * @see #paintMenubar(Graphics, String, String, int, int, int, int)
         */
        protected void paint(Graphics g) {
            try {
                setFullScreenMode(true);

                Theme theme = Registry.getManager().getTheme();
                master.paintTitleBar(g);

                // save the clipping area settings
                int clipX = g.getClipX();
                int clipY = g.getClipY();
                int clipWidth = g.getClipWidth();
                int clipHeight = g.getClipHeight();

                // calculate and set the new clipping area settings
                int titleHeight = theme.getTitleBarHeight();
                int menuHeight = theme.getMenubarHeight();
                int contentAreaHeight = master.getScreenHeight();

                contentAreaHeight = contentAreaHeight - titleHeight - menuHeight;

                g.translate(0, titleHeight);
                g.clipRect(0, 0, master.getScreenWidth(), contentAreaHeight);

                // paint the background and content area
                master.paintBackground(g);
                master.paintContentArea(g);

                // restore clip + translation
                g.translate(0, -titleHeight);
                g.setClip(clipX, clipY, clipWidth, clipHeight);

                master.paintMenubar(g);
            } catch (Throwable t) {
                // Unhandled exception in paint() will crash an application and not
                // tell you why. This lets the programmer know what caused the problem.
                Log.warn("Unhandled exception in paint for " + master, t);
            }
        }
    }

}