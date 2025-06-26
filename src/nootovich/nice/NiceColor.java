package nootovich.nice;

import java.awt.Color;

public class NiceColor {

    public static byte RED_BITDEPTH   = 6;
    public static byte GREEN_BITDEPTH = 7;
    public static byte BLUE_BITDEPTH  = 5;
    public static byte BITDEPTH       = (byte) (RED_BITDEPTH + GREEN_BITDEPTH + BLUE_BITDEPTH);

    public byte r, g, b;

    public NiceColor(Color c) {
        r = (byte) ((c.getRed() * c.getAlpha() / 255) >> (8 - RED_BITDEPTH) & 0xFF);
        g = (byte) ((c.getGreen() * c.getAlpha() / 255) >> (8 - GREEN_BITDEPTH) & 0xFF);
        b = (byte) ((c.getBlue() * c.getAlpha() / 255) >> (8 - BLUE_BITDEPTH) & 0xFF);
    }

    public boolean[] getBits() {
        boolean[] result = new boolean[BITDEPTH];
        for (int i = RED_BITDEPTH - 1; i >= 0; i--) result[RED_BITDEPTH - i - 1] = (r >> i & 0b1) > 0;
        for (int i = GREEN_BITDEPTH - 1; i >= 0; i--) result[RED_BITDEPTH + GREEN_BITDEPTH - i - 1] = (g >> i & 0b1) > 0;
        for (int i = BLUE_BITDEPTH - 1; i >= 0; i--) result[BITDEPTH - i - 1] = (b >> i & 0b1) > 0;
        return result;
    }

    public Color getColor() {
        return new Color(r, g, b);
    }
}
