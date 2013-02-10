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

/*
 * TODO: we could replace this with a constant to reduce class count instead of using the typesafe enunm pattern 
 */
/**
 * @author Greg Orlowski
 */
public class ScrollDirection {

    public static final ScrollDirection UP = new ScrollDirection();
    public static final ScrollDirection DOWN = new ScrollDirection();

    private ScrollDirection() {
    }

    // We would have to rewrite if we add more directions (e.g., horizontal scrolling)
    public String toString() {
        return this == UP ? "UP" : "DOWN";
    }
}
