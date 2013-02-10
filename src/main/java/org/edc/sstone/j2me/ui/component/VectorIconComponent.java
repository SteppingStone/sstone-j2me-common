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

import org.edc.sstone.j2me.ui.icon.VectorIcon;
import org.edc.sstone.ui.model.Dimension;

/**
 * @author Greg Orlowski
 */
public class VectorIconComponent extends Component {

    private final Dimension size;
    // private final int width;
    // private final int height;
    private final VectorIcon icon;

    public VectorIconComponent(VectorIcon icon, int width, int height) {
        this.size = new Dimension(width, height);
        this.icon = icon;
    }

    protected void paintComponent(Graphics g, int width, int height, boolean selected) {
        // works?
        icon.paint(g, 0, 0, Math.min(size.width, width), Math.min(size.height, height));
    }

    public Dimension getPreferredSize() {
        return size;
    }

}
