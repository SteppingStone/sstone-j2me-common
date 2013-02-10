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

import org.edc.sstone.j2me.ui.scroll.ScrollDirection;

/**
 * Indicates that a component can be segmented into vertical slices. This
 * 
 * @author Greg Orlowski
 */
public interface ScrollableComponent {

    /**
     * Display up to <i>viewportHeight</i> pixels of the component below or above the section that
     * is currently displayed (depending on the direction)
     * 
     * @param direction
     *            the direction to scroll
     * @param viewportHeight
     */
    public void scroll(ScrollDirection direction);

    /**
     * @return the height of the portion of this component (in pixels) above the visible portion.
     *         Clients must return 0 if the top of the component is visible. This is needed for
     *         component screens to calculate the track bar position.
     * 
     *         We cannot just subtract the component's visible height (getVisibleHeight()) from the
     *         preferred height because some of the invisible height could be BELOW the viewport. We
     *         need to actually know how much is invisible ABOVE the visible start to properly
     *         adjust the top of the scrollbarThumb when the top component is partially-scrolled
     */
    public int getHeightAboveVisibleStart();

    /**
     * @param direction
     *            the direction to scroll
     * @return true if the component can scroll in the direction else false
     */
    public boolean canScroll(ScrollDirection direction);

    /**
     * @param direction
     * @return true if there is more not-displayed content in the given direction
     */
    public boolean hasMore(ScrollDirection direction);

}
