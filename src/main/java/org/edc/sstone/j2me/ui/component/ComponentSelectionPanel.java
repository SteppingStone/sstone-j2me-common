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

import org.edc.sstone.j2me.ui.KeyCode;
import org.edc.sstone.j2me.ui.scroll.ScrollDirection;
import org.edc.sstone.j2me.ui.scroll.ScrollHandler;
import org.edc.sstone.j2me.ui.scroll.ScrollManager;
import org.edc.sstone.j2me.ui.scroll.ScrollableComponent;

/**
 * An extension of {@link ComponentContentPanel} that adds support for keeping focus on a component.
 * 
 * @author Greg Orlowski
 */
public class ComponentSelectionPanel extends ComponentContentPanel {

    protected int selectedComponentIdx = -1;

    public Component getSelectedComponent() {
        return getComponent(selectedComponentIdx);
    }

    public boolean isSelected(Component c) {
        return c == getSelectedComponent();
    }

    public boolean keyPressed(KeyCode keyCode) {

        ScrollDirection direction = ScrollManager.getScrollDirection(keyCode);
        int delta = (direction == ScrollDirection.DOWN ? 1 : -1);

        boolean ret = false;

        Component c = getSelectedComponent();
        if (c != null && c.acceptsInput() && c.keyPressed(keyCode)) {
            ret = true;
        } else if (keyCode == KeyCode.DOWN || keyCode == KeyCode.UP) {
            if ((direction == ScrollDirection.DOWN && selectedComponentIdx < scrollManager.getLastVisibleComponentIdx())
                    || (direction == ScrollDirection.UP && selectedComponentIdx > scrollManager
                            .getFirstVisibleComponentIdx())) {
                selectedComponentIdx += delta;
                return true;
            } else {
                /*
                 * TODO: This is still a little off when scrolling down to a big indivisible
                 * component. Write tests after I stabilize the UI.
                 */
                int edgeComponentIdx = getEdgeComponentIndex(direction);
                ScrollableComponent edgeComponent = getComponent(edgeComponentIdx);
                if (!edgeComponent.hasMore(direction)) {
                    edgeComponentIdx += delta;
                }
                if (scrollManager.handleContentScrolling(keyCode)) {
                    selectedComponentIdx = edgeComponentIdx;
                    ret = true;
                }
            }

        }
        return ret;
    }

    private int getEdgeComponentIndex(ScrollDirection direction) {
        return (direction == ScrollDirection.UP)
                ? scrollManager.getFirstVisibleComponentIdx()
                : scrollManager.getLastVisibleComponentIdx();
    }

    public void prepareLayout(int viewportWidth, int viewportHeight) {
        super.prepareLayout(viewportWidth, viewportHeight);
        if (selectedComponentIdx < scrollManager.getFirstVisibleComponentIdx()) {
            int i = 0;
            for (Enumeration e = getVisibleComponents(); e.hasMoreElements(); i++) {
                Component c = (Component) e.nextElement();
                if (c.acceptsInput()) {
                    selectedComponentIdx = i;
                    break;
                }
            }
        }

        if (selectedComponentIdx < scrollManager.getFirstVisibleComponentIdx()) {
            selectedComponentIdx = scrollManager.getFirstVisibleComponentIdx();
        }
    }

    public ScrollHandler getScrollHandler() {
        return scrollManager;
    }

}
