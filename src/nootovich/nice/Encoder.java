package nootovich.nice;

import java.awt.Color;
import java.awt.image.BufferedImage;
import nootovich.common.BitAccumulator;
import nootovich.common.Nice;
import nootovich.nglib.NGFileSystem;

import static nootovich.common.Nice.toBits;
import static nootovich.nice.NiceColor.*;

// NICE - Nootovich's Image Compression Encoding

public class Encoder {

    public static BufferedImage  inputImage;
    public static BitAccumulator niceImgData = new BitAccumulator();

    public static Color     prevSourceColor     = Color.BLACK;
    public static Color     nextPrevSourceColor = Color.BLACK;
    public static NiceColor prevColor           = new NiceColor(Color.BLACK);
    public static NiceColor nextPrevColor       = new NiceColor(Color.BLACK);

    public static void encode(BufferedImage inputImg, String outputPath) {
        inputImage = inputImg;
        int w = inputImg.getWidth();
        int h = inputImg.getHeight();

        niceImgData.push("NICE"); // Header
        niceImgData.push(toBits(w, 16));
        niceImgData.push(toBits(h, 16));
        niceImgData.push(toBits(RED_BITDEPTH - 1, 3));
        niceImgData.push(toBits(GREEN_BITDEPTH - 1, 3));
        niceImgData.push(toBits(BLUE_BITDEPTH - 1, 3));

        int run = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color sourceColor = new Color(inputImg.getRGB(x * inputImg.getWidth() / w, y * inputImg.getHeight() / h));
                prevSourceColor     = nextPrevSourceColor;
                nextPrevSourceColor = sourceColor;

                NiceColor color = new NiceColor(sourceColor);
                prevColor     = nextPrevColor;
                nextPrevColor = color;

                if (color.equals(prevColor)) {
                    run++;
                    continue;
                } else if (run > 0) {
                    if (run > Nice.TAG_MAX_DATA) {
                        niceImgData.push(Nice.TAG_RUN_CHUNK);
                        niceImgData.push(toBits((run / Nice.TAG_MAX_DATA) - 1, Nice.TAG_DATA_LEN));
                        run %= Nice.TAG_MAX_DATA;
                    }
                    if (run > 0) {
                        niceImgData.push(Nice.TAG_RUN);
                        niceImgData.push(toBits(run - 1, Nice.TAG_DATA_LEN));
                    }
                    run = 0;
                }

                if (-8 <= color.r - prevColor.r && color.r - prevColor.r <= 7 &&
                    -8 <= color.g - prevColor.g && color.g - prevColor.g <= 7 &&
                    -4 <= color.b - prevColor.b && color.b - prevColor.b <= 3) {
                    niceImgData.push(Nice.TAG_DIFF_SMALL);
                    niceImgData.push(toBits(color.r - prevColor.r + 8, 4));
                    niceImgData.push(toBits(color.g - prevColor.g + 8, 4));
                    niceImgData.push(toBits(color.b - prevColor.b + 4, 3));
                    continue;
                }

                niceImgData.push(Nice.TAG_COLOR);
                niceImgData.push(color.getBits());
            }
        }

        NGFileSystem.saveFile(outputPath, niceImgData.getData());
    }
}
