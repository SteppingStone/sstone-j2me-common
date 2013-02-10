/*
 * This code was originally part of the J4ME project:
 *  
 * https://code.google.com/p/j4me/
 *
 * Copyright 2007 J4ME
 * Copyright 2012 EDC
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
package org.edc.sstone.j2me.ui.style.theme;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.Constants;
import org.edc.sstone.j2me.font.FontFactory;
import org.edc.sstone.j2me.font.IFont;
import org.edc.sstone.j2me.ui.menu.MenuButton;
import org.edc.sstone.j2me.ui.style.AbstractStyle;
import org.edc.sstone.j2me.ui.util.GraphicsFunc;
import org.edc.sstone.ui.model.FontStyle;

/**
 * Subclass to set the application's theme. A theme controls the color scheme, font face and
 * background graphics used to skin the application.
 * 
 * <pre>
 *  NOTE: this class was originally borrowed from J4ME and modified by Greg Orlowski
 * </pre>
 * 
 * @author J4ME
 * @author Greg Orlowski
 */
public class Theme extends AbstractStyle {

    private static final int DARK_BLUE = 0x33344C;
    // private static final int STEEL_BLUE_DARK = 0x7B9DE7;
    private static final int STEEL_BLUE_DARK = 0x5E7EA5;

    protected static final int MEDIUM_GRAY = 0x888888;
    protected static final int BRIGHT_RED = 0xFA3C31;
    protected static final int LIGHT_GRAY = 0xF6F6F6;
    protected static final int WHITE = 0xFFFFFF;
    protected static final int GOLD = 0xFFFA39;

    protected int screenWidth;
    protected final int screenHeight;

    protected int menubarIconHeight;
    protected int menubarIconTop;
    protected int menubarTextTop;

    protected int titleBarHeight;
    protected int menubarHeight;

    protected int contentAreaTop;
    protected int contentAreaHeight;

    protected int contentHorizontalMargin;

    protected int scrollBarLeft;
    protected int scrollBarWidth;

    /**
     * The font used in the title area.
     */
    private IFont titleBarFont;

    /**
     * The font used in the menubar.
     */
    private IFont menubarFont;

    /**
     * Creates a <code>Theme</code> object. After creating the theme it must be attached to the UI
     * manager to use it through the <code>setTheme</code> method.
     * 
     */
    public Theme(FontFactory fontFactory, int screenWidth, int screenHeight) {
        super();
        fontStyle = new FontStyle(Constants.FONT_FACE_PROPORTIONAL,
                Constants.FONT_STYLE_PLAIN,
                Constants.FONT_SIZE_MEDIUM);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        reinitializeGeometry(fontFactory);
    }

    public void reinitializeGeometry(FontFactory fontFactory) {
        titleBarFont = initTitleBarFont(fontFactory, screenHeight);
        menubarFont = initMenubarFont(fontFactory, screenHeight);

        titleBarHeight = initTitleBarHeight(titleBarFont, screenWidth, screenHeight);
        menubarHeight = initMenubarHeight(menubarFont, screenWidth, screenHeight);
        menubarIconHeight = initMenubarIconHeight(menubarFont.getHeight(), screenWidth, screenHeight);

        // TODO: I could put these into methods so they could be tweaked in a subclass
        menubarIconTop = screenHeight - ((menubarHeight - menubarIconHeight) / 2) - menubarIconHeight;
        menubarTextTop = screenHeight - ((menubarHeight - menubarFont.getHeight()) / 2)
                - menubarFont.getHeight();

        contentAreaTop = titleBarHeight + 1;
        contentAreaHeight = screenHeight - titleBarHeight - menubarHeight;

        contentHorizontalMargin = initContentHorizontalMargin(screenWidth);

        scrollBarWidth = (int) (screenWidth * 0.03f);
        scrollBarLeft = screenWidth - scrollBarWidth;
    }

    protected int initContentHorizontalMargin(int screenWidth) {
        return (int) (screenWidth * 0.02f);
    }

    /**
     * <p>
     * NOTE: If you use right/left triangles as icons in the menubar, ensure that this method
     * returns an odd number because it will result in more symmetrical triangles.
     * 
     * @param
     * @param screenWidth
     * @param screenHeight
     * @return the calculated height of menubar icons
     */
    protected int initMenubarIconHeight(int menubarFontHeight, int screenWidth, int screenHeight) {
        int h = menubarFontHeight;
        return h % 2 == 0 ? h - 1 : h;
    }

    /**
     * Given the screen dimensions and menubar font, determine the height of the menubar.
     * 
     * @param menubarFont
     *            the menubar font
     * @param screenWidth
     * @param screenHeight
     * @return the height of the menubar
     */
    protected int initMenubarHeight(IFont menubarTR, int screenWidth, int screenHeight) {
        // return (int) Math.floor(menubarFont.getHeight() * 1.4f);
        return (int) (menubarTR.getHeight() * 1.2f);
    }

    /**
     * Given the screen dimensions and titleBar font, determine the height of the titleBar.
     * 
     * @param menubarFont
     *            the menubar font
     * @param screenWidth
     * @param screenHeight
     * @return the height of the menubar
     */
    protected int initTitleBarHeight(IFont titleBarTR, int screenWidth, int screenHeight) {
        return titleBarTR.getHeight() + 4;
    }

    // protected IFont initDefaultFont(int screenHeight) {
    // byte fontSize = screenHeight >= 150 ? Constants.FONT_SIZE_MEDIUM : Constants.FONT_SIZE_SMALL;
    // return fontFactory.getFont((byte) Font.FACE_PROPORTIONAL, (byte) Font.STYLE_PLAIN, fontSize);
    // }

    protected IFont initTitleBarFont(FontFactory fontFactory, int screenHeight) {
        byte fontSize = screenHeight >= 180 ? Constants.FONT_SIZE_MEDIUM : Constants.FONT_SIZE_SMALL;
        return fontFactory
                .getFont(new FontStyle(Constants.FONT_FACE_PROPORTIONAL, Constants.FONT_STYLE_BOLD, fontSize,
                        magnifyTitleBar()));
    }

    protected boolean magnifyTitleBar() {
        return false;
    }

    protected boolean magnifyMenubar() {
        return false;
    }

    /**
     * The default implementation just calls initDefaultFont(screenWidth, screenHeight);
     * 
     * @param screenWidth
     * @param screenHeight
     * @return the font that will be used in the menubar (or to calibrate the size of icons drawn in
     *         the menubar)
     */
    protected IFont initMenubarFont(FontFactory fontFactory, int screenHeight) {
        byte fontSize = screenHeight >= 140 ? Constants.FONT_SIZE_MEDIUM : Constants.FONT_SIZE_SMALL;
        return fontFactory.getFont(new FontStyle(Constants.FONT_FACE_PROPORTIONAL,
                Constants.FONT_STYLE_BOLD, fontSize, magnifyMenubar()));
    }

    public IFont getMenubarFont() {
        return menubarFont;
    }

    public IFont getTitleBarFont() {
        return titleBarFont;
    }

    /**
     * The color of the text written with the font returned by <code>getFont</code>. Colors are
     * defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of text written with the font from <code>getFont</code>.
     * @see #getFontColor()
     */
    public int getFontColor() {
        return DARK_BLUE;
    }

    /**
     * The color of the text written with the font returned by <code>getMenuFont</code>. Colors are
     * defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of text written with the font from <code>getMenuFont</code>.
     * @see #getMenubarFont()
     * @see #paintMenubar(Graphics, String, boolean, String, boolean, int, int)
     */
    public int getMenubarFontColor() {
        return WHITE;
    }

    /**
     * The color of the menu text when the menu button is pressed. Normally it will be the color
     * returned by <code>getMenuFontColor</code>. Colors are defined as 0xAARRGGBB; the first-byte
     * alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of the menu text when its menu button is pressed.
     * @see #getMenubarFont()
     * @see #getMenubarFontColor()
     * @see #paintMenubar(Graphics, String, boolean, String, boolean, int, int)
     */
    public int getMenubarFontHighlightColor() {
        return GOLD;
    }

    /**
     * The color of the text written with the font returned by <code>getTitleFont</code>. Colors are
     * defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change the text color.
     * 
     * @return The color of text written with the font from <code>getTitleFont</code>.
     * @see #getTitleFont()
     * @see #paintTrackBar(Graphics, int, int, int, int)
     */
    public int getTitleBarFontColor() {
        return getMenubarFontColor();
    }

    /**
     * Returns the color used for borders in the canvas section of the UI. An example of a border is
     * the outline around a text box. Colors are defined as 0xAARRGGBB; the first-byte alpha-channel
     * is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The color of the borders in the UI.
     */
    public int getBorderColor() {

        // TODO: temporarily set the borders to red so we see them well
        // and ensure that they are being drawn properly
        // return BRIGHT_RED;
        return MEDIUM_GRAY;
    }

    /**
     * Returns the color used as the background for the canvas section of the screen. This is the
     * area that is not the title bar at the top or menu bar at the bottom. Colors are defined as
     * 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The color of the title border.
     */
    public int getBackgroundColor() {
        return LIGHT_GRAY;
    }

    /**
     * Returns the main color used in painting components. For example a progress bar will use this
     * color to show the completed progress. Colors are defined as 0xAARRGGBB; the first-byte
     * alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The primary color used to paint components.
     */
    public int getHighlightColor() {
        // return MEDIUM_GRAY;
        return 0xCCCCCC; // medium-light gray;
    }

    /**
     * Returns the color of the border around the title bar. Colors are defined as 0xAARRGGBB; the
     * first-byte alpha-channel is ignored.
     * <p>
     * By default this is the same color as the menu border. Override this method to change it.
     * 
     * @return The color of the title border.
     * @see #paintTrackBar(Graphics, int, int, int, int)
     */
    public int getTitleBarBorderColor() {
        return getBorderColor();
    }

    /**
     * Returns the primary color of the background of the title bar. A second color defined by
     * <code>getTitleBarHighlightColor</code> is overlaid with a vertical gradient.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * By default this is the same color as the menu bar background. Override this method to change
     * it.
     * 
     * @return The primary color of the title bar background.
     * 
     * @see #getTitleBarHighlightColor()
     */
    public int getTitleBarBackgroundColor() {
        return getMenubarBackgroundColor();
    }

    /**
     * Returns the highlight color applied as a vertical gradient to the title bar.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * By default this is the same color as the menu bar highlight. Override this method to change
     * it.
     * 
     * @return The highlight color of the title bar background.
     * 
     * @see #getTitleBarBackgroundColor()
     */
    public int getTitleBarHighlightColor() {
        return getMenubarHighlightColor();
    }

    /**
     * Returns the color of the border around the menu bar. Colors are defined as 0xRRGGBB.
     * 
     * @return The color of the border around the menu bar.
     * @see #paintMenubar(Graphics, String, boolean, String, boolean, int, int)
     */
    public int getMenubarBorderColor() {
        return getBorderColor();
    }

    /**
     * Returns the primary color of the background of the menu bar. A second color defined by
     * <code>getMenubarHighlightColor</code> is overlaid with a vertical gradient.
     * <p>
     * Colors are defined as 0xRRGGBB
     * 
     * @return The color of the border around the menu bar.
     * 
     * @see #getMenubarHighlightColor()
     */
    public int getMenubarBackgroundColor() {
        return STEEL_BLUE_DARK;
    }

    /**
     * Returns the highlight color applied as a vertical gradient to the menu bar.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The highlight color of the menu bar background.
     * 
     * @see #getMenubarBackgroundColor()
     */
    public int getMenubarHighlightColor() {
        return MEDIUM_GRAY;
    }

    /**
     * Gets the height of the title bar in pixels. This method is called whenever the title is set
     * and the title bar is going to be painted.
     * 
     * @return The height of the title bar in pixels.
     * @see #paintTrackBar(Graphics, int, int, int, int)
     */
    public int getTitleBarHeight() {
        return titleBarHeight;
    }

    /**
     * Paints the title bar of the canvas. This method is called only when the title has been set
     * through <code>setTitle</code> and the canvas is not in full screen mode.
     * <p>
     * The supplied <code>Graphics</code> will be set with an appropriate clip and translated such
     * that (0,0) is the top-left corner of the title bar.
     * <p>
     * Override this method to change the appearance of the title bar. For example background or
     * logo images can be placed throughout the application by painting them here.
     * 
     * @param g
     *            the <code>Graphics</code> object to paint with.
     * @param title
     *            the text for the title bar as defined by the canvas class.
     */
    public void paintTitleBar(Graphics g, String title) {
        // Fill the background of the title bar.
        paintTitleBarBackground(g);

        // Draw a line below the title bar to separate it from the canvas.
        GraphicsFunc.drawHorizontalLine(g, getTitleBarBorderColor(), getTitleBarHeight() - 1, screenWidth);

        paintTitleText(g, title);
    }

    protected void paintTitleText(Graphics g, String title) {
        // Write the title text.
        g.setColor(getTitleBarFontColor());
        titleBarFont.drawString(g, title, contentHorizontalMargin, 1, Graphics.LEFT | Graphics.TOP);
    }

    /**
     * Paints the background area of the title bar. The text will be added later by the calling
     * <code>paintTitleBar</code> method.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     */
    protected void paintTitleBarBackground(Graphics g) {
        g.setColor(getTitleBarBackgroundColor());
        g.fillRect(0, 0, screenWidth, getTitleBarHeight());
    }

    /**
     * Gets the height of the menu bar in pixels. This method is called whenever the menu is going
     * to be painted.
     * 
     * @return The height of the menu bar in pixels.
     * 
     * @see #paintMenubar(Graphics, String, boolean, String, boolean, int, int)
     */
    public int getMenubarHeight() {
        return menubarHeight;
    }

    /**
     * Paints the menu bar at the bottom of the canvas. This method is not called if the canvas is
     * in full screen mode.
     * <p>
     * The supplied <code>Graphics</code> will be set with an appropriate clip and translated such
     * that (0,0) is the top-left corner of the title bar.
     * <p>
     * Override this method to change the appearance or functionality of the menu. Be careful not to
     * write strings that are too long and will not fit on the menu bar.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     */
    public void paintMenubar(Graphics g, MenuButton[] menuButtons) {
        // Fill the menu bar background.
        paintMenubarBackground(g);

        // Draw a line above the menu bar to separate it from the canvas.
        GraphicsFunc.drawHorizontalLine(g, getMenubarBorderColor(), getMenubarTop(), screenWidth);

        for (int i = 0; i < menuButtons.length; i++) {
            MenuButton mb = menuButtons[i];
            if (mb != null) {
                mb.paint(g, this, screenWidth);
            }
        }
    }

    /**
     * Paints the background area of the menu bar. The text will be added later by the calling
     * <code>paintMenubar</code> method.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     */
    protected void paintMenubarBackground(Graphics g) {
        g.setColor(getMenubarBackgroundColor());
        g.fillRect(0, getMenubarTop(), screenWidth, menubarHeight);

        paintMenubarButtonDividers(g);
    }

    /**
     * Paints the vertical scrollbar. The scrollbar must go on the right side of the form and span
     * from the top to the bottom of the content area.
     * <p>
     * Assume that this only gets called when the canvas <strong>has not been transposed</strong>.
     * In other words, this method will only work properly when the screen coordinates are at (0,0)
     * relative to the (top,left) corner of the canvas/display
     * </p>
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     * 
     * @param verticalOffset
     *            is the offset, measured in pixels, within totalContentHeight of the first visible
     *            element on the screen
     * 
     * @param totalContentHeight
     *            is the total height of all components on a component screen. This is greater than
     *            or equal to the height of the viewport that is actually drawn.
     */
    public void paintVerticalScrollbar(Graphics g, int verticalOffset, int totalContentHeight) {

        /*
         * NOTE: DO NOT paint relative to contentAreaTop because the screen will be translated so
         * that y==0 for the top of the content area when this method gets called.
         */
        int scrollbarWidth = getVerticalScrollbarWidth();
        paintScrollbarTrack(g);

        // draw the border
        g.setColor(getScrollbarBorderColor());
        g.drawLine(scrollBarLeft, 0, scrollBarLeft, contentAreaHeight);

        // Calculate the height of the scrollbarThumb.
        float trackPositionPercentage = (float) contentAreaHeight / (float) totalContentHeight;
        int scrollbarHeight = (int) (contentAreaHeight * trackPositionPercentage);
        scrollbarHeight = Math.max(scrollbarHeight, 2 * scrollbarWidth);
        int scrollbarTop = (int) ((float) contentAreaHeight * ((float) verticalOffset / (float) totalContentHeight));

        /*
         * Ensure that the scrollbar never runs past the total height of the track.
         * 
         * We do not use smooth scrolling. Instead, we scroll component-by-component (or by
         * component section if the component is divisible). When the top components are big and
         * take up most of the total content height, the track percentage offset may exceed the
         * percentage of the scrollbar thumb within the track. In those cases, without this
         * workaround, the scrollbar would overrun the bottom of the track.
         */
        if (scrollbarTop + scrollbarHeight > contentAreaHeight) {
            scrollbarTop = contentAreaHeight - scrollbarHeight;
        }

        paintScrollbarThumb(g, scrollbarTop, scrollbarHeight);
    }

    /**
     * Paints the background area of the scrollbar track. This does not include the scrollbar
     * itself, which will be painted after the track background is painted.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     */
    protected void paintScrollbarTrack(Graphics g) {
        // This code would paint the scrollbar a solid background.
        g.setColor(getScrollbarBackgroundColor());
        // offset by +1 from left border (and cut width by 1px) because we want
        // to draw the border at (scrollBarLeft + 0)
        g.fillRect(scrollBarLeft + 1, 0, scrollBarWidth - 1, contentAreaHeight);

    }

    /**
     * Paints the thumb of the scrollbar within the track. The thumb is the sliding bit found on the
     * scrollbar that shows the user where the current screen is relative to the scrolling.
     * 
     * @param g
     *            is the <code>Graphics</code> object to paint with.
     * @param thumbTop
     *            is the top-left Y-coordinate pixel of the scrollbarThumb.
     * @param thumbHeight
     *            is the height of the scrollbarThumb in pixels.
     * 
     * @see #paintVerticalScrollbar(Graphics, int, int, int, int, int, int)
     * @see #paintScrollbarBackground(Graphics, int, int, int, int)
     */
    protected void paintScrollbarThumb(Graphics g, int thumbTop, int thumbHeight) {
        g.setColor(getScrollbarThumbColor());
        // offset by +1 from left border (and cut width by 1px) because we want
        // to draw the border at (scrollBarLeft + 0)
        g.fillRect(scrollBarLeft + 1, thumbTop, scrollBarWidth - 1, thumbHeight);
    }

    /**
     * Returns the width of the vertical scrollbar.
     * 
     * @return The number of pixels wide the scrollbar is.
     */
    public int getVerticalScrollbarWidth() {
        return scrollBarWidth;
    }

    /**
     * Returns the color of the border around the scrollbar. Colors are defined as 0xAARRGGBB; the
     * first-byte alpha-channel is ignored.
     * <p>
     * By default this is the same as the border color. Override this method to change it.
     * 
     * @return The color of the border around the scrollbar.
     * @see #paintVerticalScrollbar(Graphics, int, int, int, int, int, int)
     */
    public int getScrollbarBorderColor() {
        return 0x686868;
    }

    /**
     * Returns the color of the background of the scrollbar track. This is the area without the
     * thumb on it.
     * <p>
     * By default this is the same as the scrollbar's border color. Override this method to change
     * it.
     * 
     * @return The color of the scrollbar background.
     */
    public int getScrollbarBackgroundColor() {
        return getMenubarBackgroundColor();
    }

    /**
     * Returns the highlight color applied as a horizontal gradient to the scrollbar.
     * <p>
     * Colors are defined as 0xAARRGGBB; the first-byte alpha-channel is ignored.
     * <p>
     * Override this method to change it.
     * 
     * @return The highlight color of the scrollbar background.
     */
    public int getScrollBarHighlightColor() {
        return getMenubarFontHighlightColor();
    }

    /**
     * Returns the color of the thumb within the scrollbar. The scrollbarThumb is the block that
     * moves up and down the scrollbar to visually inform the user of where in the scrolling they
     * are.
     * <p>
     * By default this is the same as the menu bar's background color. Override this method to
     * change it.
     * 
     * @return The color of the scrollbar's scrollbarThumb.
     */
    public int getScrollbarThumbColor() {
        return getMenubarHighlightColor();
    }

    public int getMenubarIconHeight() {
        return menubarIconHeight;
    }

    public int getMenubarIconTop() {
        return menubarIconTop;
    }

    public int getMenubarIconLeftEdge(byte pos) {
        int sixth = screenWidth / 6;
        int halfIconWidth = (menubarIconHeight / 2);

        if (pos == MenuButton.LEFT) {
            return sixth - halfIconWidth;
        } else if (pos == MenuButton.CENTER) {
            return (screenWidth / 2) - halfIconWidth;
        } else if (pos == MenuButton.RIGHT) {
            return (5 * sixth) - halfIconWidth;
        }
        return -1;
    }

    /**
     * @return margin between the right/left edge of the screen the button's text
     */
    public int getMenubarTextMargin() {
        return getMenubarFont().charWidth('W') * 2;
    }

    // public TextPosition getMenubarTextPosition(byte pos) {
    // return menubarTextPositions[pos];
    // }

    public int getMenubarTop() {
        return contentAreaTop + contentAreaHeight - 1;
    }

    protected void paintMenubarButtonDividers(Graphics g) {
        int cl = getMenubarBackgroundColor();
        int cr = getMenubarHighlightColor();

        int x = screenWidth / 3;
        int height = (int) (getMenubarHeight() * 0.7f);
        int pad = (getMenubarHeight() - height) / 2;
        int y = getMenubarTop() + pad;

        for (int i = 0; i < 2; i++) {
            g.setColor(cl);
            g.drawLine(x, y, x, y + height);
            g.setColor(cr);
            g.drawLine(x + 1, y, x + 1, y + height);
            x *= 2;
        }
    }

    /**
     * @return the usable width of the content area, which excludes margin at the screen edge and
     *         scrollbar width
     */
    public int getContentWidth() {
        return screenWidth - (contentHorizontalMargin * 2) - scrollBarWidth;
    }

    public int getContentMarginLeft() {
        return contentHorizontalMargin;
    }

    public int getPopupMenuBorderColor() {
        return getBorderColor();
    }

    public int getPopupMenuBackgroundColor() {
        return getBackgroundColor();
    }

    /*
     * TODO: refactor to Dimension
     */
    public int getPopupMenuItemWidth() {
        return screenWidth / 3;
    }

    public int getPopupMenuItemHeight() {
        return getMenubarHeight() * 2;
    }

    public int getMenubarIconColor() {
        return 0x65FF66;
    }

    public int getMenuItemDisabledColor() {
        return 0x888888;
    }

    public int getCursorColor() {
        return getMenubarBackgroundColor();
    }

    public void paintComponentFocusDecorator(Graphics g, int highlightColor, int top, int componentVisibleHeight,
            boolean acceptsInput) {
        if (acceptsInput) {
            g.setColor(highlightColor);
            int arcSize = getContentMarginLeft();
            // Left-edge square, right-edge rounded
            // TODO: account for component margin?
            // g.fillRect(0, top, getContentWidth(), componentVisibleHeight);
            g.fillRect(0, top, getContentMarginLeft() + 1, componentVisibleHeight);
            g.fillRoundRect(0, top, scrollBarLeft - 1,
                    componentVisibleHeight, arcSize, arcSize);
        } else {
            g.setColor(highlightColor);
            g.fillRect(0, top, getContentMarginLeft() - 1, componentVisibleHeight);
        }
    }

    public int getMenubarTextTop() {
        return menubarTextTop;
    }

    public long getAnimationStartDelay() {
        return 1000l;
    }

    public long getAnimationPeriod() {
        return 1000l;
    }

    public int getPopupMenuHighlightColor() {
        return 0xFFF99F; // light yellow
    }

}
