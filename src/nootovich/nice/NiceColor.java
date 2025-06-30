package nootovich.nice;

import java.awt.Color;
import nootovich.common.Nice;

public class NiceColor {

    public static byte RED_BITDEPTH   = 6;
    public static byte GREEN_BITDEPTH = 7;
    public static byte BLUE_BITDEPTH  = 5;
    public static byte RED_SHIFT      = (byte) (8 - RED_BITDEPTH);
    public static byte GREEN_SHIFT    = (byte) (8 - GREEN_BITDEPTH);
    public static byte BLUE_SHIFT     = (byte) (8 - BLUE_BITDEPTH);
    public static byte BITDEPTH       = (byte) (RED_BITDEPTH + GREEN_BITDEPTH + BLUE_BITDEPTH);

    public byte r, g, b;

    public NiceColor(Color c) {
        r = (byte) ((c.getRed() * c.getAlpha() / 255) >> (RED_SHIFT) & 0xFF);
        g = (byte) ((c.getGreen() * c.getAlpha() / 255) >> (GREEN_SHIFT) & 0xFF);
        b = (byte) ((c.getBlue() * c.getAlpha() / 255) >> (BLUE_SHIFT) & 0xFF);
    }

    public NiceColor(int r, int g, int b) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
    }

    public boolean[] getBits() {
        boolean[] result = new boolean[BITDEPTH];
        for (int i = RED_BITDEPTH - 1; i >= 0; i--) result[RED_BITDEPTH - i - 1] = (r >> i & 0b1) > 0;
        for (int i = GREEN_BITDEPTH - 1; i >= 0; i--) result[RED_BITDEPTH + GREEN_BITDEPTH - i - 1] = (g >> i & 0b1) > 0;
        for (int i = BLUE_BITDEPTH - 1; i >= 0; i--) result[BITDEPTH - i - 1] = (b >> i & 0b1) > 0;
        return result;
    }

    public Color getColor() {
        int cr = r << RED_SHIFT | (1 << RED_SHIFT) - 1;
        int cg = g << GREEN_SHIFT | (1 << GREEN_SHIFT) - 1;
        int cb = b << BLUE_SHIFT | (1 << BLUE_SHIFT) - 1;
        return new Color(cr, cg, cb);
    }

    public boolean equals(NiceColor other) {
        return other.r == this.r && other.g == this.g && other.b == this.b;
    }
}
