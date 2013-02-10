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

import org.edc.sstone.Constants;

/**
 * TODO: While I'm a big fan of the old-school typesafe enum pattern, we may want to refactor this
 * to numeric constants (maybe in {@link Constants} or UIConstants) to reduce the class
 * count.
 * 
 * @author Greg Orlowski
 */
public class VerticalScreenPosition {

    /**
     * Denotes that the component is the first visible component on the screen.
     */
    public static final VerticalScreenPosition FIRST = new VerticalScreenPosition();

    /**
     * Denotes that the component is visible but not the first or last component on the screen. This
     * does not mean that it is in the absolute middle.
     */
    public static final VerticalScreenPosition MIDDLE = new VerticalScreenPosition();

    /**
     * Denotes that the component is the last visible component on the screen.
     */
    public static final VerticalScreenPosition LAST = new VerticalScreenPosition();

    /**
     * Denotes that the component is not shown.
     */
    public static final VerticalScreenPosition NOT_SHOWN = new VerticalScreenPosition();

    private VerticalScreenPosition() {
    }
}
