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
package org.edc.sstone.j2me.ui.component;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.core.DeviceScreen;
import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.screen.ComponentScreen;
import org.edc.sstone.j2me.ui.scroll.ScrollDirection;
import org.edc.sstone.j2me.ui.scroll.ScrollableComponent;
import org.edc.sstone.j2me.ui.scroll.VerticalScreenPosition;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.ui.model.Dimension;

/**
 * Components are UI widgets that appear on forms. Examples of components include text areas, image
 * panels, reader text boxes, gauges, etc.
 * 
 * @author J4ME
 * @author Greg Orlowski
 */
public abstract class Component implements ScrollableComponent {

    private VerticalScreenPosition screenPosition = VerticalScreenPosition.NOT_SHOWN;

    /**
     * The screen this component is placed on.
     */
    private DeviceScreen screen;

    private Style componentStyle;

    protected Component() {
    }

    protected Component(Style style) {
        this.componentStyle = style;
    }

    /**
     * Implemented by the subclass to render the item within its container. At the time of the call,
     * the <code>Graphic</code>s context's destination is the content area of this
     * <code>Component</code> (or back buffer for it). The translation is set so that the upper left
     * corner of the content area is at (0,0), and the clip is set to the area to be painted. The
     * application must paint every pixel within the given clip area. The item is allowed to modify
     * the clip area, but the system must not allow any modification to result in drawing outside
     * the bounds of the item's content area. The <code>w</code> and <code>h</code> passed in are
     * the width and height of the content area of the item. These values will always be set to the
     * clip width and height and are passed here for convenience.
     * <p>
     * Other values of the <code>Graphics</code> object are as follows:
     * <ul>
     * <li>the current color is <code>Theme.getFontColor()</code>;
     * <li>the font is <code>Theme.getFont()</code>;
     * <li>the stroke style is <code>SOLID</code>;
     * </ul>
     * <p>
     * The <code>paint()</code> method will be called only when at least a portion of the item is
     * actually visible on the display.
     * 
     * @param g
     *            is the <code>Graphics</code> object to be used for rendering the item.
     * @param theme
     *            is the application's theme. Use it to get fonts and colors.
     * @param width
     *            is the width, in pixels, to paint the component.
     * @param height
     *            is the height, in pixels, to paint the component.
     * @param selected
     *            is <code>true</code> when this components is currently selected and
     *            <code>false</code> when it is not.
     */
    protected abstract void paintComponent(Graphics g, int width, int height, boolean selected);

    /**
     * Paints the component using <code>g</code>. The top-left corner is at (0,0) and the component
     * fills the rectangle bounded by <code>width</code> and <code>height</code>.
     * 
     * @param g
     *            is the <code>Graphics</code> object to be used for rendering the item.
     * @param theme
     *            is the application's theme. Use it to get fonts and colors.
     * @param screen
     *            is the screen object displaying this component.
     * @param x
     *            is the left corner pixel of the component.
     * @param y
     *            is the top corner pixel of the component.
     * @param maxWidth
     *            is the width, in pixels, to paint the component.
     * @param maxHeight
     *            is the height, in pixels, to paint the component.
     * @param selected
     *            is <code>true</code> when this components is currently selected and
     *            <code>false</code> when it is not.
     */
    public final void paint(Graphics g, int x, int y, int maxWidth, int maxHeight, boolean selected) {
        Style style = getStyle();

        // Set the graphics properties for painting the component.
        int originalClipX = g.getClipX();
        int originalClipY = g.getClipY();
        int originalClipWidth = g.getClipWidth();
        int originalClipHeight = g.getClipHeight();

        // int height = Math.min(getPreferredSize().height + (style.getPadding() * 2), maxHeight);
        int height = Math.min(getVisibleHeight() + (style.getPadding() * 2), maxHeight);

        // Paint the component's background only if it is not selected. If it is selected,
        // we will have already painted a background around component+margin with a highlight
        // color, and painting the component background on top of that screws up the rendering
        if (!selected) {
            g.setColor(style.getBackgroundColor());
            g.fillRect(x, y, maxWidth, height);
        }

        // Workaround a bug by doubling the clip height.
        // The Sun WTK clips the bottom of graphics operations for
        // rounded rectangle drawing and filling. This can be
        // avoided by doubling the clip height.

        g.setClip(originalClipX, originalClipY, originalClipWidth, originalClipHeight * 2);
        // g.setClip(originalClipX, originalClipY, originalClipWidth, originalClipHeight);

        g.translate(x, y + style.getPadding());

        g.clipRect(0, 0, maxWidth, height);
        // g.clipRect(0, 0, width, height*2);

        // paint the background:
        int originalColor = g.getColor();

        // Font originalFont = g.getFont();
        // g.setFont(theme.getFont());

        int originalStroke = g.getStrokeStyle();
        g.setStrokeStyle(Graphics.SOLID);

        // Actually paint the component.
        g.setColor(style.getFontColor());
        paintComponent(g, maxWidth, maxHeight, selected);

        // Reset the graphics properties.
        g.translate(-x, -y - style.getPadding());
        g.setClip(originalClipX, originalClipY, originalClipWidth, originalClipHeight);
        g.setColor(originalColor);
        // g.setFont(originalFont);
        g.setStrokeStyle(originalStroke);
    }

    public void setScreen(DeviceScreen screen) {
        this.screen = screen;
    }

    /**
     * Returns the desired width and height of this component in pixels. It cannot be wider than the
     * screen or it will be cropped. However, it can be taller than the screen, in which case a
     * scroll bar will be added to the screen on which this component resides.
     * 
     * @return the preferred dimensions
     */
    public abstract Dimension getPreferredSize();

    /**
     * Tells if this component accepts user input or not.
     * <p>
     * The default implementation returns <code>false</code>. Override this method to return
     * <code>true</code> if the component accepts input.
     * 
     * @return <code>true</code> if the component accepts user input; <code>false</code> if it does
     *         not.
     */
    public boolean acceptsInput() {
        return false;
    }

    /**
     * @return The screen displaying this component.
     */
    protected DeviceScreen getScreen() {
        return screen;
    }

    /**
     * @return The height, in pixels, of the visible portion of this component
     */
    public int getVisibleHeight() {
        return getPreferredSize().height;
    }

    /**
     * @param viewportWidth
     *            the width of the viewport in which we are trying to render part of this component
     * @param viewportHeight
     *            the height of the viewport in which we are trying to render part of this component
     * @return true if <strong>ANY PORTION OF THIS COMPONENT</strong> can be rendered within the
     *         viewport otherwise false.
     */
    public boolean canRenderInto(int viewportWidth, int viewportHeight) {
        return getPreferredSize().fitsWithin(viewportWidth, viewportHeight);
    }

    public int getLeftEdge(int viewportWidth) {
        int componentWidth = getPreferredSize().width;
        int hAlign = getStyle().getComponentHorizontalAnchor();

        switch (hAlign) {
            case Graphics.LEFT:
                return 0;
            case Graphics.HCENTER:
                return (viewportWidth - componentWidth) / 2;
            case Graphics.RIGHT:
                return viewportWidth - componentWidth;
        }
        return 0;

    }

    /**
     * Forces this component to repaint itself.
     */
    public void repaint() {
        if ((screen != null)) {
            // if (height != 0) {
            // Repaint just the component area since we know it.
            // screen.repaint(x, y, width, height);
            // } else {
            // Repaint the entire screen since we aren't sure where
            // the component is on it. An invalidate() was likely
            // just called so the component may move anyway.

            // TODO: optimize this, but for now repaint the entire screen
            screen.repaint();
            // }
        }
    }

    /**
     * Called when a key is pressed.
     * <p>
     * The default implementation does nothing. If a component requires keypad interaction, such as
     * to enter text, it should override this method.
     * 
     * @param keyCode
     *            is the key code of the key that was pressed.
     */
    public boolean keyPressed(KeyCode keyCode) {
        return false;
    }

    public boolean canScroll(ScrollDirection direction) {
        return false;
    }

    public void scroll(ScrollDirection direction) {
        throw new RuntimeException("You must implement this for your components to be able to scroll");
    }

    /**
     * You must implement this to support scrolling
     */
    public int getHeightAboveVisibleStart() {
        return 0;
    }

    public boolean captureFocusOnScroll() {
        return false;
    }

    public Style getStyle() {
        if (componentStyle != null)
            return componentStyle;

        if (screen != null && (screen instanceof ComponentScreen)) {
            ComponentScreen screen = (ComponentScreen) getScreen();
            return screen.getStyle();
        }
        return Registry.getManager().getTheme();
    }

    /**
     * This gets called before the component is rendered to allow divisible components to calculate
     * which portion(s) to display based on the available viewport. The default implementation does
     * nothing.
     * 
     * @param viewportWidth
     * @param viewportHeight
     */
    public int calculateVisibleHeight(int viewportWidth, int viewportHeight) {
        return getPreferredSize().height;
    }

    public boolean hasMore(ScrollDirection direction) {
        return false;
    }

    public VerticalScreenPosition getScreenPosition() {
        return screenPosition;
    }

    void setScreenPosition(VerticalScreenPosition screenPosition) {
        this.screenPosition = screenPosition;
    }

    /*
     * TODO: Is this necessary? I considered using it for audio components, but those should play on
     * screens even when they are not "visible". However, we might not want to call it for other
     * components unless they are actually in the visible viewport. Think through this some more.
     */
    // public void showNotify() {
    // }

    protected IFont getFont() {
        return Registry.getManager().getFontFactory().getFont(getStyle().getFontStyle());
    }

}
