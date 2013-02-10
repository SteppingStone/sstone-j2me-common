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
package org.edc.sstone.j2me.device.nokia;

import java.util.Timer;
import java.util.TimerTask;

import org.edc.sstone.j2me.device.BacklightControl;

import com.nokia.mid.ui.DeviceControl;

/**
 * @author Greg Orlowski
 */
public class NokiaDeviceControl implements BacklightControl {

    private Timer timer = new Timer();
    private NokiaBacklightKeepAliveTask backlightTask;
    private static final int PERIOD_SECONDS = 10;
    private int keepAliveSeconds = 0;

    public synchronized void stayLit() {
        boolean needsReset = false;

        if (keepAliveSeconds == 0) {
            return;
        }

        if (backlightTask != null) {
            synchronized (backlightTask) {
                if (backlightTask.count >= 1) {
                    backlightTask.count = keepAliveSeconds / PERIOD_SECONDS;
                } else {
                    backlightTask.cancel();
                    needsReset = true;
                }
            }
        }

        if (backlightTask == null || needsReset) {
            DeviceControl.setLights(0, 100);
            backlightTask = new NokiaBacklightKeepAliveTask(keepAliveSeconds);
            long period = PERIOD_SECONDS * 1000l;
            timer.schedule(backlightTask, 0l, period);
        }
    }

    private static class NokiaBacklightKeepAliveTask extends TimerTask {

        private int count;

        NokiaBacklightKeepAliveTask(int seconds) {
            this.count = seconds / PERIOD_SECONDS;
        }

        public void run() {
            synchronized (NokiaBacklightKeepAliveTask.this) {
                if (--count <= 0) {
                    DeviceControl.setLights(0, 0);
                    NokiaBacklightKeepAliveTask.this.cancel();
                }
                // otherwise, if count > 0, do nothing
            }
        }
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public void cleanup() {
        if (backlightTask != null) {
            try {
                backlightTask.cancel();
            } catch (Exception ignore) {
            } finally {
                backlightTask = null;
            }
        }
        if (timer != null) {
            try {
                timer.cancel();
            } catch (Exception ignore) {
            } finally {
                timer = null;
            }
        }
    }
}
