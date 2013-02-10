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
package org.edc.sstone.j2me.store;

import org.edc.sstone.CheckedException;
import org.edc.sstone.Constants;
import org.edc.sstone.event.Observer;
import org.edc.sstone.j2me.ui.screen.ErrorMessageScreen;
import org.edc.sstone.log.Log;
import org.edc.sstone.store.ValueSource;

/**
 * @author Greg Orlowski
 */
public class ValueSourceObserver implements Observer {

    private final int recordId;
    private final ValueSource valueSource;
    private final String propertyName;

    public ValueSourceObserver(String propertyName, int recordId, ValueSource valueSource) {
        this.recordId = recordId;
        this.valueSource = valueSource;
        this.propertyName = propertyName;
    }

    public void update(Object value) {
        try {
            valueSource.setValue(recordId, value);
        } catch (CheckedException me) {
            handleException(me);
        }
    }

    protected void handleException(CheckedException me) {
        if (me.code == CheckedException.PREF_DB_WRITE_ERROR) {
            Log.warn(me.getMessage(), me);
            ErrorMessageScreen.forMessagekey("rms.write.error", new String[] { propertyName },
                    Constants.NAVIGATION_DIRECTION_RELOAD_CURR).show();
        } else {
            // TODO: replace this with an error key that takes the exception message as an arg
            ErrorMessageScreen.forMessagekey("application.error", Constants.NAVIGATION_DIRECTION_EXIT).show();
        }
    }

}
