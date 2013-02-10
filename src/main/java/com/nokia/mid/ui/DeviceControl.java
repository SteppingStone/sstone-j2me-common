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
package com.nokia.mid.ui;

import org.edc.sstone.log.Log;

/**
 * A stub class for nokia device backlight controls
 */
public class DeviceControl {

    /**
     * num - this should be 0 for the backlight level - 0 means off and 100 should mean keep it
     * bright
     */
    public static void setLights(int num, int level) {
        Log.error("Celled our stub nokia DeviceControl#setLights method, not the REAL one.");
    }

}
