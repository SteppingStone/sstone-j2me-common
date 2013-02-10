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
package org.edc.sstone.j2me.ui.scroll;

import java.util.Vector;

import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.screen.ComponentScreen;

/**
 * Manages the scrolling position and handles scrolling events and scrollbar painting for a
 * {@link ComponentScreen}
 * 
 * TODO: unit test this
 * 
 * @author Greg Orlowski
 */
public class ScrollManager implements ScrollHandler {

    // int viewportHeight;

    int firstVisibleComponentIdx = 0;
    int visibleComponentCount = -1;

    private final ScrollPosition scrollPos = new ScrollPosition(-1, -1);

    boolean scrollingNeeded = false;

    public boolean userScrollingEnabled = true;
    private Vector components;

    public ScrollManager(Vector components) {
        this.components = components;
    }

    public void setScrollBarPos(int scrollbarThumbPos, int totalContentHeight) {
        scrollPos.setPosition(scrollbarThumbPos, totalContentHeight);
    }

    public void setVisibleComponentState(int firstVisibleComponentIdx, int visibleComponentCount) {
        this.firstVisibleComponentIdx = firstVisibleComponentIdx;
        this.visibleComponentCount = visibleComponentCount;
        if (components.size() == 0) {
            this.scrollingNeeded = false;
        } else {
            int lastVisibleComponent = firstVisibleComponentIdx + (visibleComponentCount - 1);
            this.scrollingNeeded = (firstVisibleComponentIdx > 0
                    || visibleComponentCount < components.size()
                    || getComponent(lastVisibleComponent).hasMore(ScrollDirection.DOWN)
                    || getComponent(firstVisibleComponentIdx).hasMore(ScrollDirection.UP));
        }
    }

    public int getFirstVisibleComponentIdx() {
        return firstVisibleComponentIdx;
    }

    public int getLastVisibleComponentIdx() {
        return firstVisibleComponentIdx + visibleComponentCount - 1;
    }

    public boolean isScrollingNeeded() {
        return scrollingNeeded;
    }
    
    public void resetToTop() {
        firstVisibleComponentIdx = 0;
    }

    public boolean canScroll(ScrollDirection direction) {
        if (!isScrollingNeeded())
            return false;

        if (canScroll(direction, components.size())) {
            return true;
        }

        return canScrollEdgeComponent(direction);
    }

    private boolean canScrollEdgeComponent(ScrollDirection direction) {
        ScrollableComponent c = getEdgeComponent(direction);
        /*
         * If this is the last component, we must not scroll down past the point at which the last
         * segment of the component is visible at the bottom of the screen. If the bottom component
         * is NOT the last visible component, we should support scrolling down through the component
         * until the last line is at the top of the screen and we continue to show more components
         * below.
         */
        if (direction == ScrollDirection.DOWN
                && (getLastVisibleComponentIdx() == components.size() - 1)) {
            return c.hasMore(ScrollDirection.DOWN);
        }
        return c.canScroll(direction);
    }

    private ScrollableComponent getEdgeComponent(ScrollDirection direction) {
        int idx = direction == ScrollDirection.UP
                ? getFirstVisibleComponentIdx()
                : getLastVisibleComponentIdx();
        return getComponent(idx);
    }

    public boolean handleContentScrolling(KeyCode keyCode) {
        if (keyCode == KeyCode.UP || keyCode == KeyCode.DOWN) {
            // if (isScrollingNeeded() && isUserScrollingEnabled()) {
            if (isScrollingNeeded()) {
                ScrollDirection direction = getScrollDirection(keyCode);
                if (canScroll(direction)) {
                    scroll(direction);
                    return true;
                }
            }
        }
        return false;
    }

    // TODO: put this elsewhere
    public static ScrollDirection getScrollDirection(KeyCode keyCode) {
        return keyCode == KeyCode.UP ? ScrollDirection.UP : ScrollDirection.DOWN;
    }

    private boolean canScroll(ScrollDirection direction, int totalComponentCount) {
        if (direction == ScrollDirection.UP)
            return firstVisibleComponentIdx > 0;
        else if (direction == ScrollDirection.DOWN)
            return (firstVisibleComponentIdx + visibleComponentCount) < totalComponentCount;
        return false;
    }

    public void scroll(ScrollDirection direction) {
        ScrollableComponent topComponent = getComponent(getFirstVisibleComponentIdx());

        if (topComponent.canScroll(direction)) {
            topComponent.scroll(direction);
        } else {
            recalculateVisibleStartIdx(direction, components.size());
        }
    }

    private ScrollableComponent getComponent(int idx) {
        return (ScrollableComponent) components.elementAt(idx);
    }

    private void recalculateVisibleStartIdx(ScrollDirection direction, int componentSize) {
        firstVisibleComponentIdx += (direction == ScrollDirection.UP ? -1 : 1);
    }

    /*
     * TODO: How should we do user scrolling toggle? Simple setter+boolean? Polymorphism (getter no
     * setter)?
     */
    // Is this needed?
    // public boolean isUserScrollingEnabled() {
    // return userScrollingEnabled;
    // }

    // public void setUserScrollingEnabled(boolean enableUserScrolling) {
    // this.userScrollingEnabled = enableUserScrolling;
    // }

    public ScrollPosition getScrollPosition() {
        return scrollPos;
    }

}
