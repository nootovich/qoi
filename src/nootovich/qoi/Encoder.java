package nootovich.qoi;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javax.imageio.ImageIO;
import nootovich.nglib.NGFileSystem;

public class Encoder {
    public static final short QOI_OP_INDEX = 0b00000000;
    public static final short QOI_OP_DIFF  = 0b01000000;
    public static final short QOI_OP_LUMA  = 0b10000000;
    public static final short QOI_OP_RUN   = 0b11000000;
    public static final short QOI_OP_RGB   = 0b11111110;
    public static final short QOI_OP_RGBA  = 0b11111111;

    public static Stack<Byte> qoiImgData    = new Stack<>();
    public static Color[]     seenPixels    = new Color[64];
    public static Color       prevPixel     = Color.BLACK;
    public static Color       nextPrevPixel = Color.BLACK;

    static {
        for (int i = 0; i < 64; i++) seenPixels[i] = Color.BLACK;
    }

    public static void encode(String inputImage, String outputPath) throws IOException {

        BufferedImage inputImg = ImageIO.read(new File(inputImage));
        int           w        = inputImg.getWidth();
        int           h        = inputImg.getHeight();
        int           run      = 0;

        // QOI Header
        qoiPush('q'); qoiPush('o'); qoiPush('i'); qoiPush('f');
        qoiPushInt(w); qoiPushInt(h);
        qoiPush(3); // 3 channels (RGB)
        qoiPush(0); // sRGB colorspace

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color pixel = new Color(inputImg.getRGB(x * inputImg.getWidth() / w, y * inputImg.getHeight() / h));
                prevPixel     = nextPrevPixel;
                nextPrevPixel = pixel;

                // RUN
                if (pixel.getRGB() == prevPixel.getRGB()) {
                    run++;
                    continue;
                } else if (run > 0) {
                    qoiPushOpRun(run);
                    run = 0;
                }

                // INDEX
                int index_position = (pixel.getRed() * 3 + pixel.getGreen() * 5 + pixel.getBlue() * 7 + pixel.getAlpha() * 11) % 64;
                if (seenPixels[index_position].getRGB() == pixel.getRGB()) {
                    qoiPushOpIndex(index_position);
                    continue;
                } else {
                    seenPixels[index_position] = pixel;
                }

                // DIFF
                byte dr = (byte) (pixel.getRed() - prevPixel.getRed());
                byte dg = (byte) (pixel.getGreen() - prevPixel.getGreen());
                byte db = (byte) (pixel.getBlue() - prevPixel.getBlue());
                if (-2 <= dr && dr <= 1 && -2 <= dg && dg <= 1 && -2 <= db && db <= 1) {
                    qoiPushOpDiff(dr, dg, db);
                    continue;
                }

                // LUMA
                byte dr_dg = (byte) (dr - dg);
                byte db_dg = (byte) (db - dg);
                if (-32 <= dg && dg <= 31 && -8 <= dr_dg && dr_dg <= 7 && -8 <= db_dg && db_dg <= 7) {
                    qoiPushOpLuma(dg, dr_dg, db_dg);
                    continue;
                }

                // RGB
                qoiPushOpRGB(pixel);
            }
        }

        if (run > 0) qoiPushOpRun(run);

        // QOI Byte stream end
        qoiPushInt(0); qoiPushInt(1);

        NGFileSystem.saveFile(outputPath, qoiImgData.toArray(new Byte[]{ }));
    }

    public static void qoiPush(int n) {
        qoiImgData.push((byte) (n & 0xFF));
    }

    public static void qoiPushInt(int n) {
        qoiImgData.push((byte) (n >> 24 & 0xFF));
        qoiImgData.push((byte) (n >> 16 & 0xFF));
        qoiImgData.push((byte) (n >> 8 & 0xFF));
        qoiImgData.push((byte) (n & 0xFF));
    }

    public static void qoiPushOpRGB(Color color) {
        qoiPush(QOI_OP_RGB);
        qoiPush(color.getRed());
        qoiPush(color.getGreen());
        qoiPush(color.getBlue());
    }

    public static void qoiPushOpRGBA(Color color) {
        qoiPush(QOI_OP_RGBA);
        qoiPush(color.getRed());
        qoiPush(color.getGreen());
        qoiPush(color.getBlue());
        qoiPush(color.getAlpha());
    }

    public static void qoiPushOpIndex(int index) {
        qoiPush(QOI_OP_INDEX | index);
    }

    public static void qoiPushOpDiff(int dr, int dg, int db) {
        qoiPush(QOI_OP_DIFF | ((dr + 2) << 4) | ((dg + 2) << 2) | db + 2);
    }

    public static void qoiPushOpLuma(int dg, int dr_dg, int db_dg) {
        qoiPush(QOI_OP_LUMA | (dg + 32));
        qoiPush((dr_dg + 8) << 4 | (db_dg + 8));
    }

    public static void qoiPushOpRun(int run) {
        qoiPush(QOI_OP_RUN | run - 1);
    }
}
