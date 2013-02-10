/*
 * Copyright (c) 2005-2009 Sergey Tkachev http://sergetk.net
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.sergetk.mobile.lcdui;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.apache.commons.lang.IntHashMap;
import org.edc.sstone.cache.CacheEntry;
import org.edc.sstone.log.Log;
import org.edc.sstone.util.StdLib;

/**
 * <p>
 * BitmapFont allows developers to use his own fonts in mobile applications.
 * </p>
 * 
 * <p>
 * This class includes a mixed set of methods of Graphics and Font classes. It has character
 * measurement methods from Font, such as stringWidth() and charWidth(). It also has text drawing
 * methods from Graphics, such as drawString() and drawChar(). Text will be drawn by current
 * Graphics color.
 * </p>
 * 
 * <p>
 * You may create your own font using Bitmap Character Editor. This is crossplatform desktop
 * application written on Java using SWT library. It can be downloaded free from project site.
 * </p>
 * 
 * <p>
 * Each bitmap font consists of a set of parameters such as height, baseline position etc, of
 * character code map, of array width character widthes and of principal part: one or more images in
 * PNG format store character outlines.
 * </p>
 * 
 * <p>
 * By default all fonts are considered normal. Bold, italic and bold italic styles are generated
 * programmaticaly.
 * </p>
 * 
 * <p>
 * This code is a part of the <a href="http://sourceforge.net/projects/mobilefonts">Mobile Fonts
 * Project</a>.
 * </p>
 * 
 * <p>
 * Note: if this code was useful to you, write me please. I will be proud :)
 * </p>
 * 
 * <pre>
 * Changelog (Greg Orlowski):
 *  - Added Image getColorizedFontImage(Image sourceImage, int color) method to support
 *    font files that do not use the exact encoding supported by the png-header manipualtion
 *    code written by SergeTK. This is just a fallback
 *  - Refactored font caching code
 * </pre>
 * 
 * @author Sergey Tkachev <a href="http://sergetk.net">http://sergetk.net</a>
 * @author Greg Orlowski
 */
public class BitmapFont {
    private final static int DEFAULT_COLOR_CACHE_CAPACITY = 5;

    private String fontFilePath;
    private Image baseImage;
    private Image currentImage;

    private int height;
    private int baseline;
    private int xIndent;
    private int yIndent;
    private int spaceWidth;

    private int style;
    private int currentColor;
    private int charWidthIncrement = 0;

    private String characterMap;
    private int[] widths, x, y;
    protected byte version;

    private short pngOffset;

    /*
     * TODO: replace LRU with LFU cache algorithm?
     */
    private CacheEntry[] colorCache;
    private final IntHashMap colorUsageCounts;

    private boolean italic;
    private boolean bold;

    private static BitmapFont defaultFont;

    /**
     * Gets the default font
     * 
     * @return the default font
     */
    public static BitmapFont getDefault() {
        return defaultFont;
    }

    /**
     * Set a font as default
     * 
     * @param font
     *            the font
     */
    public static void setDefault(BitmapFont font) {
        defaultFont = font;
    }

    private BitmapFont(BitmapFont font, int style) {
        this.fontFilePath = font.fontFilePath;
        this.currentImage = this.baseImage = font.baseImage;

        this.height = font.height;
        this.baseline = font.baseline;
        this.xIndent = font.xIndent;
        this.yIndent = font.yIndent;
        this.spaceWidth = font.spaceWidth;
        this.pngOffset = font.pngOffset;

        this.style = style;
        this.italic = (style & Font.STYLE_ITALIC) != 0;
        this.bold = (style & Font.STYLE_BOLD) != 0;
        this.currentColor = 0;

        this.characterMap = font.characterMap;
        this.widths = font.widths;
        this.x = font.x;
        this.y = font.y;

        this.colorCache = font.colorCache;
        this.colorUsageCounts = font.colorUsageCounts;
        this.charWidthIncrement = bold ? 1 : 0;
    }

    /**
     * Creates a new font from the resource.
     * 
     * @param fontName
     *            the resource name
     */
    public BitmapFont(String fontName) {
        this(fontName, DEFAULT_COLOR_CACHE_CAPACITY);
    }

    /**
     * Creates a new font from the resource. The capacity of the color cache defines maximum size of
     * the color cache.
     * 
     * @param fontPath
     *            the resource name
     * @param colorCacheCapacity
     *            the maximum color cache size
     */
    public BitmapFont(String fontPath, int colorCacheCapacity) {
        this.style = Font.STYLE_PLAIN;
        this.currentColor = 0;
        this.colorCache = new CacheEntry[colorCacheCapacity];
        this.colorUsageCounts = new IntHashMap(colorCacheCapacity * 2);

        try {
            InputStream input = new Object().getClass().getResourceAsStream(fontPath);
            if (input == null) {
                throw new IOException();
            }

            DataInputStream data = new DataInputStream(input);

            int streamLen = data.available();

            this.fontFilePath = fontPath;

            this.version = data.readByte();
            this.height = data.readByte();
            this.baseline = data.readByte();
            this.xIndent = data.readByte();
            this.yIndent = data.readByte();
            this.spaceWidth = data.readByte();

            characterMap = data.readUTF();
            int count = characterMap.length();

            // read characters widthes
            this.widths = new int[count];
            this.x = new int[count];
            this.y = new int[count];

            for (int i = 0; i < count; i++) {
                widths[i] = data.readByte();
            }

            baseImage = null;

            // the original implementation supported multiple-images
            // in the font file, but this is not necessary. Because I do
            // not want to change the encoding, I am leaving this byte that
            // used to represent the number of PNGs in the file
            data.skipBytes(1);

            short pngLen = data.readShort();
            byte[] buffer = new byte[pngLen];

            data.read(buffer, 0, pngLen);
            this.pngOffset = (short) (streamLen - pngLen);
            baseImage = Image.createImage(buffer, 0, pngLen);
            currentImage = baseImage;

            // calculate characters coordinates
            int curX = 0, curY = 0;
            for (int i = 0; i < count; i++) {
                if (widths[i] < 0) {
                    // negative width points to another character
                    int sourceIndex = -widths[i];
                    widths[i] = widths[sourceIndex];
                    x[i] = x[sourceIndex];
                    y[i] = y[sourceIndex];
                } else {
                    x[i] = curX;
                    y[i] = curY;
                    curX += widths[i];
                }
            }

            if (defaultFont == null)
                defaultFont = this;
        } catch (IOException e) {
            // Log.warn("IOException reading font: ", e);
            System.err.println("IOException reading font: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * If the style differs from the current style, create a new font instance with the new style
     * with minimal memory consumption. Otherwise return this object.
     * 
     * @param style
     *            the style of the font
     * @return the font
     */
    public BitmapFont getFont(int style) {
        return (style == getStyle()) ? this : new BitmapFont(this, style);
    }

    private void markUsage(CacheEntry cacheEntry) {
        cacheEntry.markUsage();
        colorUsageCounts.put(cacheEntry.cacheId, new Integer(cacheEntry.getUsageCount()));
    }

    protected void setColor(int color) {
        color &= 0x00FFFFFF;
        if (this.currentColor == color) {
            return;
        }

        this.currentColor = color;
        if (color == 0x00000000) { // new color is black
            this.currentImage = this.baseImage;
        } else {
            int cacheItemIndex = 0;
            int minUsage = Integer.MAX_VALUE;
            for (int i = 0; i < colorCache.length; i++) {
                if (colorCache[i] == null) {
                    cacheItemIndex = i;
                    break;
                } else if (colorCache[i].cacheId == color) {
                    // the color is already in the cache
                    currentImage = (Image) colorCache[i].object;
                    markUsage(colorCache[i]);
                    return;
                } else if (colorCache[i].getUsageCount() < minUsage) {
                    minUsage = colorCache[i].getUsageCount();
                    cacheItemIndex = i;
                }
            }

            Image colorizedFontImage = null;
            colorizedFontImage = getColorizedFontImage(this.fontFilePath, this.pngOffset, color);
            if (colorizedFontImage == null) {
                Log.warn("Could not get colorized font image by processing png color pallete. Using backup method.");
                colorizedFontImage = getColorizedFontImage(this.baseImage, color);
            }

            // long startTime = System.currentTimeMillis();
            // for (int i = 0; i < 100; i++) {
            // colorizedFontImage = getColorizedFontImage(this.baseImage, color);
            // colorizedFontImage = getColorizedFontImage(this.fontFilePath, this.pngOffset, color);
            // }
            // long endTime = System.currentTimeMillis();
            // Log.debug("Font colorization time: " + (endTime - startTime));

            colorCache[cacheItemIndex] = new CacheEntry(colorizedFontImage, color);
            int usageCount = 1;
            if (colorUsageCounts.containsKey(color)) {
                usageCount = ((Integer) colorUsageCounts.get(color)).intValue();
            }
            colorCache[cacheItemIndex].setUsageCount(usageCount);
            this.currentImage = colorizedFontImage;
        }
    }

    private static final String PNG_SIGNATURE = "\u0089PNG\r\n\u001A\n";

    private static boolean compareBytes(byte[] buffer, int offset, String str) {
        for (int i = 0; i < str.length(); i++) {
            if (((byte) (str.charAt(i))) != buffer[i + offset]) {
                return false;
            }
        }
        return true;
    }

    private static void colorizePalette(byte[] buffer, int offset, int color) {
        int dataLength = StdLib.bytesToInt(buffer, offset);
        int dataOffset = offset + 8;

        int r = (color & 0x00FF0000) >>> 16;
        int g = (color & 0x0000FF00) >>> 8;
        int b = (color & 0x000000FF);

        for (int i = 0; i < dataLength / 3; i++) {
            int pR = buffer[dataOffset + 0] & 0xFF;
            int pG = buffer[dataOffset + 1] & 0xFF;
            int pB = buffer[dataOffset + 2] & 0xFF;

            int brightness = (pR + pG + pB) / 3;

            buffer[dataOffset++] = (byte) (r + (brightness * (255 - r)) / 255); // red
            buffer[dataOffset++] = (byte) (g + (brightness * (255 - g)) / 255); // green
            buffer[dataOffset++] = (byte) (b + (brightness * (255 - b)) / 255); // blue
        }

        int crc = crc32(buffer, offset + 4, dataLength + 4);
        StdLib.copyIntInto(crc, buffer, offset + 8 + dataLength);
    }

    private static final int CRC32_POLYNOMIAL = 0xEDB88320;

    private static int crc32(byte buffer[], int offset, int count) {
        int crc = 0xFFFFFFFF;
        while (count-- != 0) {
            int t = (crc ^ buffer[offset++]) & 0xFF;
            for (int i = 8; i > 0; i--) {
                if ((t & 1) == 1) {
                    t = (t >>> 1) ^ CRC32_POLYNOMIAL;
                } else {
                    t >>>= 1;
                }
            }
            crc = (crc >>> 8) ^ t;
        }
        return crc ^ 0xFFFFFFFF;
    }

    private static Image getColorizedFontImage(String path, short skip, int color) {
        InputStream inputStream = BitmapFont.class.getResourceAsStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buff = new byte[2048];
        int bytesRead = 0;
        try {
            inputStream.skip(skip);
            while ((bytesRead = inputStream.read(buff)) != -1) {
                bos.write(buff, 0, bytesRead);
            }
            return getColorizedImage(bos.toByteArray(), color);
        } catch (IOException ioe) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignoreCloseFailure) {
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ignoreCloseFailure) {
                }
            }
        }
        return null;
    }

    private static Image getColorizedImage(byte[] imageBuffer, int color) {
        Image ret = null;

        if (!compareBytes(imageBuffer, 0, PNG_SIGNATURE)) {
            return null;
        }

        int paletteOffset = getChunk(imageBuffer, 8, "PLTE");
        if (paletteOffset >= 0) {
            colorizePalette(imageBuffer, paletteOffset, color);
            ret = Image.createImage(imageBuffer, 0, imageBuffer.length);
        }

        return ret;
    }

    private static int getChunk(byte[] buffer, int offset, String chunk) {
        try {
            for (;;) {
                int dataLenght = StdLib.bytesToInt(buffer, offset);
                if (compareBytes(buffer, offset + 4, chunk)) {
                    return offset;
                } else {
                    offset += 4 + 4 + dataLenght + 4;
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    // For a 1317*15 image, this will create an int array that will consume about
    // 77k... ouch. It should be a fast operation, but the large temporary-memory
    // consumption may be a deal breaker. The memory allocation+gc might make it slow.
    private static Image getColorizedFontImage(Image sourceImage, int color) {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();
        int scanlength = width;
        int[] rgbData = new int[width * height];

        sourceImage.getRGB(rgbData, 0, scanlength, 0, 0, width, height);
        int transparentWhite = 0x00FFFFFF;

        for (int i = 0; i < rgbData.length; i++) {
            // if the pixel is opaque (alpha channel > 0) then colorize the pixel.
            // Otherwise, set it to transparent white.
            rgbData[i] = (rgbData[i] & 0xFF000000) != 0
                    ? 0xFF000000 | color
                    : transparentWhite;
        }
        return Image.createRGBImage(rgbData, width, height, true);
    }

    /**
     * Gets the style of the font.
     * 
     * @return style
     */
    public int getStyle() {
        return this.style;
    }

    /**
     * Gets the standard height of a line of a text in this font.
     * 
     * @return the height in pixels
     */
    public int getHeight() {
        return height + yIndent;
    }

    /**
     * Gets the index of the character.
     * 
     * @param c
     *            the character
     * @return the index of the character
     */
    protected int charIndex(char c) {
        try {
            return characterMap.indexOf(c);
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }

    /**
     * Gets the distance from the top of the text to the text baseline.
     * 
     * @return the baseline position in pixels
     */
    public int getBaselinePosition() {
        return baseline;
    }

    /**
     * Draws the specified string.
     * 
     * @param g
     *            the graphics context
     * @param text
     *            the text to be drawn
     * @param x
     *            the x coordinate of the anchor point
     * @param y
     *            the y coordinate of the anchor point
     * @param anchors
     *            the anchor point for positioning of the text
     * @return the x coordinate for the next string
     */
    public int drawString(Graphics g, String text, int x, int y, int anchors) {
        return drawSubstring(g, text, 0, text.length(), x, y, anchors);
    }

    /**
     * Draws the specified substring.
     * 
     * @param g
     *            the graphics context
     * @param text
     *            the text to be drawn
     * @param offset
     *            the index of a first character
     * @param length
     *            the number of characters
     * @param x
     *            the x coordinate of the anchor point
     * @param y
     *            the y coordinate of the anchor point
     * @param anchors
     *            the anchor point for positioning the text
     * @return the x coordinate for the next string
     */
    public int drawSubstring(Graphics g, String text, int offset, int length, int x, int y, int anchors) {
        int xx = getX(substringWidth(text, offset, length), x, anchors);
        int yy = getY(y, anchors);
        setColor(g.getColor());
        for (int i = offset; i < offset + length; i++) {
            xx = drawOneChar(g, text.charAt(i), xx, yy);
        }
        if ((style & Font.STYLE_UNDERLINED) != 0) {
            int yU = y + this.baseline + 2;
            g.drawLine(x, yU, xx - 1, yU);
        }
        return xx;
    }

    private int getX(int w, int x, int anchors) {
        if ((anchors & Graphics.RIGHT) != 0) {
            return x - w;
        } else if ((anchors & Graphics.HCENTER) != 0) {
            return x - w / 2;
        }
        return x;
    }

    private int getY(int y, int anchors) {
        if ((anchors & Graphics.BOTTOM) != 0) {
            return y - height;
        } else if ((anchors & Graphics.VCENTER) != 0) {
            return y - height / 2;
        } else if ((anchors & Graphics.BASELINE) != 0) {
            return y - this.getBaselinePosition();
        }
        return y;
    }

    /**
     * Draws the specified character.
     * 
     * @param g
     *            the graphics context
     * @param c
     *            the character to be drawn
     * @param x
     *            the x coordinate of the anchor point
     * @param y
     *            the y coordinate of the anchor point
     * @return the x coordinate for the next character
     */
    public int drawChar(Graphics g, char c, int x, int y) {
        setColor(g.getColor());
        int nextX = drawOneChar(g, c, x, y);
        if ((style & Font.STYLE_UNDERLINED) != 0) {
            int yU = y + this.baseline + 2;
            g.drawLine(x, yU, nextX - 1, yU);
        }
        return nextX;
    }

    /**
     * Draws one character. It called from drawChar(), drawString() and drawSubstrung().
     * 
     * @param g
     *            the graphics context
     * @param c
     *            the character to be drawn
     * @param x
     *            the x coordinate of the anchor point
     * @param y
     *            the y coordinate of the anchor point
     * @return the x coordinate for the next character
     */
    protected int drawOneChar(Graphics g, char c, int x, int y) {
        // skip if it is a space
        if (c == ' ') {
            return x + this.spaceWidth + xIndent + charWidthIncrement;
        }
        int charIndex = charIndex(c);
        // draw the unknown character as a rectangle
        if (charIndex < 0) {
            int squareWidth = this.spaceWidth + xIndent + charWidthIncrement;
            g.drawRect(x, y, squareWidth - 1, height - 1);
            return x + squareWidth;
        }

        int charX = this.x[charIndex];
        int charY = this.y[charIndex];
        int cw = widths[charIndex];

        y += yIndent / 2;

        Image image = this.currentImage;

        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipWidth = g.getClipWidth();
        int clipHeight = g.getClipHeight();

        int ix = x - charX;
        int iy = y - charY;

        if (!italic && !bold) {
            g.clipRect(x, y, cw, this.height);
            g.drawImage(image, ix, iy, Graphics.LEFT | Graphics.TOP);
        } else if (italic & bold) {
            int halfHeight = height / 2;
            g.clipRect(x + 1, y, cw, this.height);
            g.drawImage(image, ix + 1, iy, Graphics.LEFT | Graphics.TOP);
            g.setClip(clipX, clipY, clipWidth, clipHeight);
            g.clipRect(x + 2, y, cw, halfHeight);
            g.drawImage(image, ix + 2, iy, Graphics.LEFT | Graphics.TOP);
            g.setClip(clipX, clipY, clipWidth, clipHeight);
            g.clipRect(x, y + halfHeight, cw, height - halfHeight);
            g.drawImage(image, ix, iy, Graphics.LEFT | Graphics.TOP);
        } else if (italic) {
            int halfHeight = height / 2;
            g.clipRect(x + 1, y, cw, halfHeight);
            g.drawImage(image, ix + 1, iy, Graphics.LEFT | Graphics.TOP);
            g.setClip(clipX, clipY, clipWidth, clipHeight);
            g.clipRect(x, y + halfHeight, cw, height - halfHeight);
            g.drawImage(image, ix, iy, Graphics.LEFT | Graphics.TOP);
        } else { // just a bold
            g.clipRect(x, y, cw, this.height);
            g.drawImage(image, ix, iy, Graphics.LEFT | Graphics.TOP);
            g.setClip(clipX, clipY, clipWidth, clipHeight);
            g.clipRect(x + 1, y, cw, this.height);
            g.drawImage(image, ix + 1, iy, Graphics.LEFT | Graphics.TOP);
        }
        // restore clipping
        g.setClip(clipX, clipY, clipWidth, clipHeight);
        return x + cw + xIndent + charWidthIncrement;
    }

    /**
     * Draws the specified characters.
     * 
     * @param g
     *            the graphics context
     * @param data
     *            the array of characters to be drawn
     * @param offset
     *            the start offset in the data
     * @param length
     *            the number of characters to be drawn
     * @param x
     *            the x coordinate of the anchor point
     * @param y
     *            the y coordinate of the anchor point
     * @param anchors
     *            the anchor point for positioning the text
     * @return the x coordinate for the next character
     */
    public int drawChars(Graphics g, char[] data, int offset, int length, int x, int y, int anchors) {
        int xx = getX(charsWidth(data, offset, length), x, anchors);
        int yy = getY(y, anchors);
        setColor(g.getColor());
        for (int i = offset; i < offset + length; i++) {
            xx = drawOneChar(g, data[i], xx, yy);
        }
        if ((style & Font.STYLE_UNDERLINED) != 0) {
            int yU = y + this.baseline + 2;
            g.drawLine(x, yU, xx - 1, yU);
        }
        return xx;
    }

    /* ================= Character measurement functions =============== */

    /**
     * Gets the width of the specified character in this font.
     * 
     * @param c
     *            the character to be measured
     * @return the width of the character
     */
    public int charWidth(char c) {
        if (c == ' ') {
            return spaceWidth + xIndent + charWidthIncrement;
        }
        int index = charIndex(c);
        if (index < 0) {
            return spaceWidth + xIndent + charWidthIncrement;
        } else {
            return widths[index] + xIndent + charWidthIncrement;
        }
    }

    /**
     * Gets the width of the characters, starting at the specified offset and for the specified
     * number of characters (length).
     * 
     * @param ch
     *            the array of characters
     * @param offset
     *            zero-based index of a first character
     * @param length
     *            the number of characters to measure
     * @return the width in pixels
     */
    public int charsWidth(char[] ch, int offset, int length) {
        int w = 0;
        for (int i = offset; i < offset + length; i++) {
            w += charWidth(ch[i]);
        }
        return w;
    }

    /**
     * Gets the width of the string.
     * 
     * @param str
     *            the String to be measured
     * @return the width in pixels
     */
    public int stringWidth(String str) {
        return substringWidth(str, 0, str.length());
    }

    /**
     * Gets the width of the substring.
     * 
     * @param str
     *            the string to be measured
     * @param offset
     *            zero-based index of a first character in the substring
     * @param length
     *            the number of characters to measure
     * @return the length of the substring
     */
    public int substringWidth(String str, int offset, int length) {
        int w = 0;
        for (int i = offset; i < offset + length; i++) {
            w += charWidth(str.charAt(i));
        }
        return w;
    }

}
