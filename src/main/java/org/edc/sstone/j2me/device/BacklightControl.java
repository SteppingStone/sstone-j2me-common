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
package org.edc.sstone.j2me.device;

/**
 * @author Greg Orlowski
 */
public interface BacklightControl {

    /**
     * Send a keepalive event to keep the backlight from shutting off. The backlight will continue
     * to be lit for n number of seconds, where n == the value set by
     * {@link #setKeepAliveSeconds(int)}
     */
    public void stayLit();

    /**
     * Set the number of seconds that should elapse between the most recent user event (e.g.,
     * keypress) or application event (e.g., animation frame advance) and the time that the screen
     * backlight should be turned off. A value of 0 means use the default device settings.
     */
    public void setKeepAliveSeconds(int keepAliveSeconds);

    /**
     * Clean up any timers or other resources used by this object.
     */
    public void cleanup();
}
