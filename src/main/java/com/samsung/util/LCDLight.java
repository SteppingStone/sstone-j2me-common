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
package com.samsung.util;

/**
*   A stub class for samsung j2me LCD backlight controls.
*
*   See: <a href="http://www.j2megame.org/j2meapi/Samsung_API_1_0/com/samsung/util/LCDLight.html">The Samsung Javadoc</a>
*/
public class LCDLight {

    public static boolean isSupported() {
        return true;
    }

    public static void off() {
    }

    public static void on(int duration) {
    }

}
