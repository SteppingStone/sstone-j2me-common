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

import org.edc.sstone.j2me.ui.scroll.ScrollDirection;
import org.edc.sstone.j2me.ui.style.Style;

/**
 * @author Greg Orlowski
 */
public abstract class EvenlyDivisibleComponent extends Component {

    /**
     * The index of the first segment to display (inclusive)
     */
    protected int visibleRangeStart = 0;

    /**
     * Denotes the last segment to display (exclusive). I.e., == (last line to display +1)
     */
    private int visibleRangeEnd = 0;

    protected EvenlyDivisibleComponent(Style style) {
        super(style);
    }

    public boolean canScroll(ScrollDirection direction) {
        if (direction == ScrollDirection.UP) {
            return visibleRangeStart > 0;
        } else if (direction == ScrollDirection.DOWN) {
            // This would scroll down past the component
            // return visibleRangeEnd < getVerticalSegmentCount();

            // segment-by-segment scroll
            return visibleRangeStart < (visibleRangeEnd - 1);
        }
        return false;
    }

    public void scroll(ScrollDirection direction) {
        int idxDelta = (direction == ScrollDirection.UP) ? -1 : 1;
        visibleRangeStart += idxDelta;

        if (direction == ScrollDirection.UP) {
            if (getSegmentHeight(getVisibleSegmentCount()) > getScreen().getHeight()) {
                visibleRangeEnd--;
            }
        } else if (direction == ScrollDirection.DOWN) {
            if (visibleRangeEnd < getVerticalSegmentCount())
                visibleRangeEnd++;
        }
    }

    protected abstract int getSegmentHeight(int segmentCount);

    protected abstract int getVerticalSegmentCount();

    public int getVisibleHeight() {
        return getSegmentHeight(getVisibleSegmentCount());
    }

    public int getHeightAboveVisibleStart() {
        return getSegmentHeight(visibleRangeStart);
    }

    /**
     * Return true if at least 1 line fits within the viewport
     */
    public boolean canRenderInto(int viewportWidth, int viewportHeight) {
        return getSegmentHeight(1) <= viewportHeight;
    }

    public int calculateVisibleHeight(int viewportWidth, int availableViewportHeight) {
        // if (visibleRangeStart == 0 && getPreferredSize().height > viewportHeight) {
        if (visibleRangeStart == 0) {
            clipFromTop(availableViewportHeight);
        }
        return getVisibleHeight();
    }

    private void clipFromTop(int availableViewportHeight) {
        int segmentCount = getVisibleSegmentCount(visibleRangeStart, availableViewportHeight);
        visibleRangeEnd = Math.min(visibleRangeStart + segmentCount, getVerticalSegmentCount());
    }

    protected int getVisibleSegmentCount() {
        return visibleRangeEnd - visibleRangeStart;
    }

    protected abstract int getVisibleSegmentCount(int startIdx, int viewportHeight);

    protected void setVisibleRangeEnd(int visibleRangeEnd) {
        this.visibleRangeEnd = visibleRangeEnd;
    }

    protected int getVisibleRangeEnd() {
        return visibleRangeEnd;
    }

    public boolean hasMore(ScrollDirection direction) {
        return (direction == ScrollDirection.UP)
                ? visibleRangeStart > 0
                : visibleRangeEnd < getVerticalSegmentCount();
    }

}
