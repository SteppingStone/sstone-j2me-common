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
package org.edc.sstone.j2me.font;

import java.util.Enumeration;

import javax.microedition.lcdui.Font;

import net.sergetk.mobile.lcdui.BitmapFont;

import org.apache.commons.lang.IntHashMap;
import org.edc.sstone.Constants;
import org.edc.sstone.ui.model.FontStyle;

/**
 * @author Greg Orlowski
 */
public class FontFactory {

    public static final byte FONT_IMPL_SYSTEM = 1;
    public static final byte FONT_IMPL_BITMAP = 2;

    private final byte[] fontSizes; // must be null or a 3-member array
    private final byte adapterType;
    private byte magnification = 1;

    // TODO: after v1, I could consider writing a true cache
    // abstraction that keeps statistics of hits/misses and supports
    // switching between LRU and LFU. I could add a hotkey to the app
    // to pull up a memory profile screen
    private final int cacheCapacity = 6; // TODO: after v1, make this tuneable

    private IntHashMap cacheLRUTracker = new IntHashMap(6);
    private IntHashMap fontCache = new IntHashMap(6);

    public FontFactory(byte fontEngine, byte[] fontSizes, byte magnification) {
        this.adapterType = fontEngine;
        this.fontSizes = fontSizes;
        this.magnification = magnification;
    }

    protected String bitmapFontPath = "/fonts/raster";

    protected int makeCacheKey(FontStyle style, boolean systemFont) {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + style.getFace();
        result = (prime * result) + style.getSize();
        if (systemFont) {
            result = (prime * result) + style.getStyle();
        }
        return result;
    }

    protected void putInCache(IFont ifont, int cacheKey) {
        if (fontCache.size() >= cacheCapacity) {
            long lru = System.currentTimeMillis();
            int keyToRemove = -1;
            for (Enumeration e = cacheLRUTracker.keys(); e.hasMoreElements();) {
                int key = ((Integer) e.nextElement()).intValue();
                long lastUsage = ((Long) cacheLRUTracker.get(key)).longValue();
                if (lastUsage < lru) {
                    lru = lastUsage;
                    keyToRemove = key;
                }
            }
            fontCache.remove(keyToRemove);
        }
        cacheLRUTracker.put(cacheKey, new Long(System.currentTimeMillis()));
        fontCache.put(cacheKey, ifont);
    }

    /**
     * @param fontSize
     *            one of {@link Constants#FONT_SIZE_SMALL}, {@link Constants#FONT_SIZE_MED}, or
     *            {@link Constants#FONT_SIZE_LARGE}
     * @return
     */
    public IFont getFont(FontStyle fontStyle) {
        fontStyle = fontStyle.magnified(magnification);

        /*
         * For v1.0, monospace automatically uses system fonts. We probably only need it to align
         * simple math equations that use ASCII chars. This is just a time-saving measure because
         * rendering and then image-correcting raster fonts from TTFs is time consuming.
         */
        boolean useSystemFont = (adapterType == FONT_IMPL_SYSTEM
                || fontStyle.getFace() == Font.FACE_MONOSPACE);
        IFont delegateFont = null;

        int cacheKey = -1;
        Object iFontObj = null;

        if (!useSystemFont) {
            cacheKey = makeCacheKey(fontStyle, useSystemFont);
            iFontObj = fontCache.get(cacheKey);
        }

        if (iFontObj != null) {
            // Log.debug("Found font in cache: " + cacheKey);
            delegateFont = (IFont) iFontObj;
        } else if (useSystemFont) {
            // Log.debug("Creating new system font: " + cacheKey);
            Font font = Font.getFont(fontStyle.getFace(), fontStyle.getStyle(), fontStyle.getSize());
            delegateFont = new SystemFontAdapter(font);
        } else if (adapterType == FONT_IMPL_BITMAP) {
            // Log.debug("Creating new bitmap font: " + cacheKey);
            int sizeIdx = 1;
            switch (fontStyle.getSize()) {
                case Constants.FONT_SIZE_SMALL:
                    sizeIdx = 0;
                    break;
                case Constants.FONT_SIZE_LARGE:
                    sizeIdx = 2;
                    break;
            }
            int sizePx = fontSizes[sizeIdx];
            String fontFaceName = fontStyle.getFace() == Font.FACE_MONOSPACE ? "monospace" : "proportional";

            // always initialize with a plain style. The plain-style variant should be the cached
            // variant to reduce object instantiation since we will probably use plain the most.
            delegateFont = new BitmapFontAdapter(loadBitmapFont(fontFaceName, sizePx));
        }

        // If not in the cache, put in the cache, otherwise, update the lru timestamp
        if (!useSystemFont) {
            if (iFontObj == null) {
                putInCache(delegateFont, makeCacheKey(fontStyle, useSystemFont));
            } else {
                cacheLRUTracker.put(cacheKey, new Long(System.currentTimeMillis()));
            }
        }

        if ((delegateFont instanceof BitmapFontAdapter) && fontStyle.getStyle() != delegateFont.getStyle()) {
            delegateFont = new BitmapFontAdapter(((BitmapFontAdapter) delegateFont).font.getFont(fontStyle.getStyle()));
        }

        return new DelegatingFontAdapter(delegateFont, fontStyle);
    }

    protected BitmapFont loadBitmapFont(String name, int fontHeightPx) {
        return new BitmapFont(getFontPath(name, fontHeightPx, "fnt"));
    }

    protected String getFontPath(String name, int fontHeightPx, String ext) {
        return bitmapFontPath + "/" + getFontName(name, fontHeightPx) + '.' + ext;
    }

    protected String getFontName(String name, int fontHeightPx) {
        return name + "_" + fontHeightPx;
    }

    public void setMagnification(byte magnification) {
        this.magnification = magnification;
    }

}
