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

import java.util.Enumeration;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.audio.AudioPlayer;
import org.edc.sstone.j2me.core.DeviceScreen;
import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.component.Component;
import org.edc.sstone.j2me.ui.component.ComponentContentPanel;
import org.edc.sstone.j2me.ui.component.ComponentPanel;
import org.edc.sstone.j2me.ui.menu.MenuButton;
import org.edc.sstone.j2me.ui.menu.PopupMenuButton;
import org.edc.sstone.j2me.ui.scroll.ScrollPosition;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.j2me.ui.style.theme.Theme;
import org.edc.sstone.ui.model.Spacing;

public class ComponentScreen extends DeviceScreen {

    // private Vector components = new Vector();
    protected ComponentPanel componentPanel;
    // protected ScrollManager scrollManager;
    // protected ComponentSelector componentSelector;

    protected Style style;

    public ComponentScreen() {
        this(null, new ComponentContentPanel());
    }

    public ComponentScreen(Style style, ComponentPanel componentPanel) {
        this.style = style;
        this.componentPanel = componentPanel;
    }

    public Style getStyle() {
        return style != null
                ? style
                : super.getStyle();
    }

    public void addComponent(Component component) {
        component.setScreen(this);
        componentPanel.addComponent(component);
        if (isShown()) {
            // repainting will automatically update the last visible component
            // index. NOTE: adding a component in the middle or at the top
            // would require the first component idx to be recalculated and
            // the screen to be repainted (currently unsupported)
            repaint();
        }
    }

    /**
     * OVERRIDE
     */
    public void showNotify() {
        super.showNotify();
        componentPanel.showNotify();
    }

    /**
     * OVERRIDE
     */
    public void hideNotify() {
        super.hideNotify();
        componentPanel.hideNotify();
        // Always stop the audio player (if it is running) when we switch screens.
        stopAudio();
    }

    protected void stopAudio() {
        AudioPlayer ap = getAudioPlayer();
        if (ap != null) {
            ap.cleanup();
        }
    }

    protected AudioPlayer getAudioPlayer() {
        return Registry.getManager().getAudioPlayer();
    }

    protected void keyPressed(KeyCode keyCode) {
        super.keyPressed(keyCode);
        boolean handled = sendKeyCodeToPopupMenu(keyCode);
        if (!handled) {
            handled = sendKeyCodeToComponentPanel(keyCode);
        }
        if (!handled) {
            handled = sendKeyCodeToMenuButton(keyCode);
        }
    }

    /**
     * By default, the only keycodes that we will repeat are for scrolling, which we will send to
     * the component panel.
     */
    protected void keyRepeated(KeyCode keyCode) {
        if (getVisiblePopupMenu() == null) {
            sendKeyCodeToComponentPanel(keyCode);
        }
    }

    protected boolean sendKeyCodeToPopupMenu(KeyCode keyCode) {
        PopupMenuButton popupMenu = getVisiblePopupMenu();
        if (popupMenu != null && popupMenu.isEnabled()) {
            return popupMenu.keyPressed(keyCode);
        }
        return false;
    }

    protected boolean sendKeyCodeToComponentPanel(KeyCode keyCode) {
        if (componentPanel.keyPressed(keyCode)) {
            repaint();
            return true;
        }
        return false;
    }

    protected boolean sendKeyCodeToMenuButton(KeyCode keyCode) {
        MenuButton mb = null;
        if (keyCode == KeyCode.MENU_LEFT) {
            mb = menuButtons[MenuButton.LEFT];
        } else if (keyCode == KeyCode.FIRE) {
            mb = menuButtons[MenuButton.CENTER];
        } else if (keyCode == KeyCode.MENU_RIGHT) {
            mb = menuButtons[MenuButton.RIGHT];
        }
        if (mb != null && mb.isEnabled())
            return mb.keyPressed(keyCode);
        return false;
    }

    protected PopupMenuButton getVisiblePopupMenu() {
        for (int i = 0; i < menuButtons.length; i++) {
            if ((menuButtons[i] instanceof PopupMenuButton)
                    && ((PopupMenuButton) menuButtons[i]).isVisible()) {
                return ((PopupMenuButton) menuButtons[i]);
            }
        }
        return null;
    }

    protected void paintComponents(Graphics g, DeviceScreen screen,
            int x, int y, int viewportWidth, int viewportHeight) {

        componentPanel.prepareLayout(viewportWidth, viewportHeight);

        int availableViewportHeight = viewportHeight;
        int visibleHeight = 0;
        int heightPlusVSpace = 0;
        boolean selected = false;
        Spacing margin;

        for (Enumeration e = componentPanel.getVisibleComponents(); e.hasMoreElements();) {
            Component c = (Component) e.nextElement();

            selected = componentPanel.isSelected(c);

            int padding = c.getStyle().getPadding();
            visibleHeight = c.getVisibleHeight() + (padding * 2);

            margin = c.getStyle().getMargin();
            heightPlusVSpace = (margin.getTop() + visibleHeight + margin.getBottom());

            // TODO: if bottom or top component, I should do differently?
            // actually, it seems OK for now
            if (selected) {
                paintComponentFocusDecorator(g, getStyle().getHighlightColor(), y + margin.getTop(),
                        visibleHeight, c.acceptsInput());
            }

            c.paint(g, x, y + margin.getTop(), viewportWidth, availableViewportHeight, selected);
            y += heightPlusVSpace;
            availableViewportHeight -= heightPlusVSpace;
        }
    }

    private void paintComponentFocusDecorator(Graphics g, int highlightColor,
            int y, int visibleHeight, boolean acceptsInput) {
        getTheme().paintComponentFocusDecorator(g, highlightColor, y, visibleHeight, acceptsInput);
    }

    // private void paintComponentSelectionDecorator(Graphics g, Component c, int x, int y, int
    // viewportWidth) {
    // int visibleHeight = c.getVisibleHeight();
    // g.setColor(0x00FF00);
    // for (int x2 = 0; x2 < 2; x2++)
    // g.drawLine(x2, y, x2, y + visibleHeight);
    // }

    protected void paintContentArea(Graphics g) {
        Theme theme = getTheme();

        int marginLeft = theme.getContentMarginLeft();

        // g.translate(marginLeft, 0);

        // TODO: should we get the clip here then clip the area
        // within the right+left margins? This should not be necessary
        // if our components behave properly

        // Clip to exclude the horizontal margin and scrollbar area
        // I need to render the scrollbar before this
        // g.clipRect(0, 0, theme.getContentWidth(), g.getClipHeight());

        paintComponents(g, this, marginLeft, 0, theme.getContentWidth(), g.getClipHeight());

        // TODO: restore the clip, and translate back to the origin
        // g.translate(-marginLeft, 0);

        paintScrollBar(g, theme);
    }

    public void paintScrollBar(Graphics g, Theme theme) {
        ScrollPosition scrollPos = componentPanel.getScrollPosition();
        if (scrollPos != null) {
            theme.paintVerticalScrollbar(g, scrollPos.getThumbOffset(), scrollPos.getTotalContentHeight());
        }
    }

    /*
     * TODO: rename?: layout? panel? model? manager? viewmodel? passiveView?
     */
    // protected ComponentPanel getComponentModel() {
    // return componentPanel;
    // }

}
