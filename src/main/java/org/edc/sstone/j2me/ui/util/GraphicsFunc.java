/*
 * Some of this code was originally part of the J4ME project:
 *
 * https://code.google.com/p/j4me/
 *
 * Copyright 2007 J4ME
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.  See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.edc.sstone.j2me.ui.util;

import javax.microedition.lcdui.Graphics;

import org.edc.sstone.j2me.font.IFont;

public class GraphicsFunc {

    public static void strikeThrough(Graphics g, IFont font, int x, int y, int anchor, int width) {
        int lineHeight = font.getHeight() / 6;
        int lineTop = y;
        int lineLeft = x;
        int i;

        switch (anchor & (Graphics.BASELINE | Graphics.VCENTER | Graphics.BOTTOM | Graphics.TOP)) {
        // TODO: Baseline is going to be a little off. We can fix this later.
            case Graphics.BASELINE:
            case Graphics.BOTTOM:
                lineTop = y - (font.getHeight() / 2);
                break;
            case Graphics.TOP:
                lineTop = y + (font.getHeight() / 2);
                break;
            case Graphics.VCENTER:
                lineTop = y;
                break;
        }

        for (i = 0; i < lineHeight / 2; i++)
            lineTop--;

        switch (anchor & (Graphics.LEFT | Graphics.HCENTER | Graphics.RIGHT)) {
            case Graphics.LEFT:
                lineLeft = x;
                break;
            case Graphics.HCENTER:
                lineLeft = x - (width / 2);
                break;
            case Graphics.RIGHT:
                lineLeft = x - width;
                break;
        }

        for (i = 0; i < lineHeight; i++) {
            g.drawLine(lineLeft, lineTop + i, lineLeft + width, lineTop + i);
        }

    }

    /**
     * A convenience function to draw a line across the entire width of the screen.
     * 
     * @param g
     * @param color
     * @param y
     * @param screenWidth
     */
    public static void drawHorizontalLine(Graphics g, int color, int y, int screenWidth) {
        g.setColor(color);
        g.drawLine(0, y, screenWidth, y);
    }

    /**
     * Draw a quadrilateral (can be a trapezoid, a rhombus, a parellelogram, or any quadrilateral
     * with non-parallel sides). Note that quadrilaterals can always be drawn as 2 triangles.
     * 
     * @param g
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @param x4
     * @param y4
     */
    public static void fillQuadrilateral(Graphics g, int x1, int y1,
            int x2, int y2,
            int x3, int y3,
            int x4, int y4) {
        g.fillTriangle(x1, y1, x2, y2, x3, y3);
        g.fillTriangle(x1, y1, x3, y3, x4, y4);
    }

    /**
     * Fills a rectangle with linear gradient. The gradient colors go from <code>primaryColor</code>
     * to <code>secondaryColor</code> at <code>maxSecondary</code>. So if
     * <code>maxSecondary == 0.70</code> then a line across the fill rectangle 70% of the way would
     * be <code>secondaryColor</code>.
     * 
     * NOTE: this code was taken from J4ME
     * 
     * @param g
     *            is the <code>Graphics</code> object for painting.
     * @param x
     *            is the left edge of the rectangle.
     * @param y
     *            is the top edge of the rectangle.
     * @param width
     *            is the width of the rectangle.
     * @param height
     *            is the height of the rectangle.
     * @param fillVertically
     *            is <code>true</code> if the gradient goes from top-to-bottom or <code>false</code>
     *            for left-to-right.
     * @param primaryColor
     *            is the main color.
     * @param secondaryColor
     *            is the highlight color.
     * @param maxSecondary
     *            is between 0.0 and 1.0 and says how far down the fill will
     *            <code>secondaryColor</code> peak. Peak of 0.0 puts the secondary color at the top
     *            and 1.0 puts the secondary color at the bottom.
     */
    public static void gradientFill(
            Graphics g,
            int x, int y, int width, int height,
            boolean fillVertically,
            int primaryColor, int secondaryColor, float maxSecondary) {
        // Break the primary color into red, green, and blue.
        int pr = (primaryColor & 0xFF0000) >> 16;
        int pg = (primaryColor & 0x00FF00) >> 8;
        int pb = (primaryColor & 0x0000FF);

        // Break the secondary color into red, green, and blue.
        int sr = (secondaryColor & 0xFF0000) >> 16;
        int sg = (secondaryColor & 0x00FF00) >> 8;
        int sb = (secondaryColor & 0x0000FF);

        // Draw a horizonal line for each pixel from the top to the bottom.
        int end = (fillVertically ? height : width);

        for (int i = 0; i < end; i++) {
            // Calculate the color for this line.
            float p = (float) i / (float) end;
            float v = Math.abs(maxSecondary - p);
            float v2 = 1.0f - v;

            int red = (int) (pr * v + sr * v2);
            int green = (int) (pg * v + sg * v2);
            int blue = (int) (pb * v + sb * v2);

            g.setColor(red, green, blue);

            // Draw the line.
            if (fillVertically) {
                g.drawLine(x, y + i, x + width, y + i);
            } else {
                // horizontal
                g.drawLine(x + i, y, x + i, y + height);
            }
        }
    }
}
