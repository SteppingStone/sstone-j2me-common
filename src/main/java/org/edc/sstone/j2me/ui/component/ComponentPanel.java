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
import org.edc.sstone.j2me.ui.scroll.ScrollPosition;

/**
 * @author Greg Orlowski
 */
public interface ComponentPanel {

    public boolean isSelected(Component c);

    public void addComponent(Component c);

    public Enumeration getVisibleComponents();

    /**
     * Call this before you paint components to calculate which ones are visible.
     * 
     * @param viewportWidth
     * @param viewportHeight
     */
    public void prepareLayout(int viewportWidth, int viewportHeight);

    public ScrollPosition getScrollPosition();

    public boolean keyPressed(KeyCode keyCode);

    public Component getSelectedComponent();

    public void showNotify();

    public void hideNotify();

    public void setAllowUserScrolling(boolean allow);

}