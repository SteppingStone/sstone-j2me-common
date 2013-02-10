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
package org.edc.sstone.j2me.ui.component;

import java.util.Enumeration;
import java.util.Vector;

import org.edc.sstone.j2me.core.DeviceScreen;
import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.scroll.ScrollManager;
import org.edc.sstone.j2me.ui.scroll.ScrollPosition;
import org.edc.sstone.j2me.ui.scroll.VerticalScreenPosition;
import org.edc.sstone.log.Log;
import org.edc.sstone.ui.model.Spacing;

/**
 * @author Greg Orlowski
 */
public class ComponentContentPanel implements ComponentPanel {

    private final Vector components = new Vector();
    protected ScrollManager scrollManager;

    public ComponentContentPanel() {
        this.scrollManager = new ScrollManager(components);
    }

    public void addComponent(Component c) {
        components.addElement(c);
    }

    public Enumeration getVisibleComponents() {
        return new VisibleComponentEnumeration(scrollManager, this);
    }

    protected static class VisibleComponentEnumeration implements Enumeration {

        int i;
        private final ScrollManager scrollManager;
        private final ComponentContentPanel panel;

        protected VisibleComponentEnumeration(ScrollManager scrollManager, ComponentContentPanel panel) {
            i = scrollManager.getFirstVisibleComponentIdx();
            this.scrollManager = scrollManager;
            this.panel = panel;
        }

        public boolean hasMoreElements() {
            return i <= scrollManager.getLastVisibleComponentIdx();
        }

        public Object nextElement() {
            return panel.getComponent(i++);
        }

    }

    public void prepareLayout(final int viewportWidth, final int viewportHeight) {

        int availableViewportHeight = viewportHeight;

        int firstVisibleComponentIdx = scrollManager.getFirstVisibleComponentIdx();
        int visibleComponentCount = 0;
        int totalContentHeight = 0;
        int scrollbarThumbPos = 0;
        int totalComponentHeight = 0;

        int padding = 0;
        Spacing margin;

        // we always want to loop through all components because we need to get
        // the total height to calculate scrollbar state
        for (int i = 0; i < components.size(); i++) {
            Component c = getComponent(i);
            margin = c.getStyle().getMargin();
            padding = c.getStyle().getPadding();

            int preferredHeight = c.getPreferredSize().height + (padding * 2);
            int preferredWidth = c.getPreferredSize().width + (padding * 2);
            totalComponentHeight = margin.getTop() + preferredHeight + margin.getBottom();
            int totalComponentWidth = margin.getLeft() + preferredWidth + margin.getRight();

            /*
             * If the component cannot be divided to render into the viewport, replace it with
             * a warning message.
             */
            if ((totalComponentHeight > viewportHeight || totalComponentWidth > viewportWidth)
                    && !c.canRenderInto(viewportWidth, viewportHeight)) {
                String errorMessage = Registry
                        .getManager()
                        .getMessageSource()
                        .getString(
                                "component.does.not.fit",
                                new Object[] { new Integer(i + 1), new Integer(totalComponentWidth),
                                        new Integer(totalComponentHeight) });
                Component warningMessageTextArea = c = new TextArea(errorMessage, viewportWidth);
                components.setElementAt(warningMessageTextArea, i);
                totalComponentHeight = preferredHeight = warningMessageTextArea.getPreferredSize().height;
                totalComponentWidth = preferredWidth = warningMessageTextArea.getPreferredSize().width;
                Log.warn(errorMessage);
            }

            if (i < firstVisibleComponentIdx) {
                // scrollbarThumbPos += (preferredHeight + margin.getTop() + margin.getBottom());
                scrollbarThumbPos += totalComponentHeight;
            }

            int maxAvailableComponentHeight = availableViewportHeight
                    - (margin.getTop() + margin.getBottom() + (padding * 2));

            // an optimization. Once we know that a given component does not
            // fit in the remaining viewport height, set availableViewportHeight
            // to 0 to stop rendering all subsequent components
            // (avoids all subsequent components
            if (availableViewportHeight > 0 && !c.canRenderInto(viewportWidth, maxAvailableComponentHeight))
                availableViewportHeight = 0;

            // if the component for index is at or below the first component to
            // display and we have remaining screen real-estate, mark the component in the visible
            // range
            if (i >= firstVisibleComponentIdx && availableViewportHeight > 0) {

                if (i == firstVisibleComponentIdx) {
                    scrollbarThumbPos += c.getHeightAboveVisibleStart();
                    c.setScreenPosition(VerticalScreenPosition.FIRST);
                } else {
                    c.setScreenPosition(VerticalScreenPosition.MIDDLE);
                }

                totalComponentHeight = margin.getTop()
                        + c.calculateVisibleHeight(viewportWidth, maxAvailableComponentHeight)
                        + margin.getBottom()
                        + (padding * 2);

                availableViewportHeight -= totalComponentHeight;
                visibleComponentCount++;
            } else {
                c.setScreenPosition(VerticalScreenPosition.NOT_SHOWN);
            }

            // use the preferred height for scrollbar total height calculation
            // totalContentHeight += (preferredHeight + margin.getTop() + margin.getBottom());
            totalContentHeight += (margin.getTop() + preferredHeight + margin.getBottom());
        }

        scrollManager.setVisibleComponentState(firstVisibleComponentIdx, visibleComponentCount);
        scrollManager.setScrollBarPos(scrollbarThumbPos, totalContentHeight);

        if (getComponents().size() > 0) {
            getComponent(scrollManager.getLastVisibleComponentIdx()).setScreenPosition(VerticalScreenPosition.LAST);
        }
    }

    protected final Component getComponent(int i) {
        return components.size() > 0 ? (Component) components.elementAt(i) : null;
    }

    public ScrollPosition getScrollPosition() {
        return scrollManager.isScrollingNeeded() ? scrollManager.getScrollPosition() : null;
    }

    public boolean keyPressed(KeyCode keyCode) {
        if (scrollManager.userScrollingEnabled && (keyCode == KeyCode.DOWN || keyCode == KeyCode.UP)) {
            return scrollManager.handleContentScrolling(keyCode);
        }
        return false;
    }

    public boolean isSelected(Component c) {
        return false;
    }

    public Component getSelectedComponent() {
        // this implementation does not support
        // component-selection. Just return null.
        return null;
    }

    protected Vector getComponents() {
        return components;
    }

    /**
     * Does nothing. Override to customize.
     */
    public void showNotify() {
    }

    /**
     * Does nothing. Override to customize.
     */
    public void hideNotify() {
    }

    public void setAllowUserScrolling(boolean allow) {
        scrollManager.userScrollingEnabled = allow;
    }

    protected DeviceScreen getScreen() {
        return (components == null || components.isEmpty())
                ? null
                : getComponent(0).getScreen();
    }

}
