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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.ui.model.Dimension;
import org.edc.sstone.ui.model.Spacing;

/*
 * TODO: rework "divisible" and segmented components. Account for the case where 
 * an image does not fit entirely on a single screen and draw portions of it in an offscreen buffer 
 * 
 * Actually, what would be even better would be to add a layoutmanager system for components to support
 * gravity (north, center or south) and auto-space components (not for v1.0)
 */
/**
 * @author Greg Orlowski
 */
public class ImagePanel extends Component {

    private final Image image;
    private Dimension dimension;

    public ImagePanel(Image image) {
        this(null, image);
    }

    public ImagePanel(Style style, Image image) {
        super(style);
        this.image = image;
        recalculateDimensions();
    }

    private void recalculateDimensions() {
        int w = image.getWidth();
        int h = image.getHeight();
        Spacing margin = getStyle().getMargin();
        dimension = new Dimension(w, h + margin.getTop() + margin.getBottom());
    }

    public int getAnchorX(int viewportWidth) {
        int hAlign = getStyle().getComponentHorizontalAnchor();

        switch (hAlign) {
            case Graphics.LEFT:
                return 0;
            case Graphics.HCENTER:
                return viewportWidth / 2;
            case Graphics.RIGHT:
                return viewportWidth;
        }
        return 0;
    }

    protected void paintComponent(Graphics g, int width, int height, boolean selected) {
        int anchor = getStyle().getComponentAnchor();
        g.drawImage(image, getAnchorX(width),
                0 + getStyle().getMargin().getTop(),
                anchor);
    }

    public Dimension getPreferredSize() {
        return dimension;
    }

    public int getVisibleHeight() {
        return getPreferredSize().height;
    }
}
