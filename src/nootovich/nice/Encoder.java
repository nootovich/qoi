package nootovich.nice;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import nootovich.common.BitAccumulator;
import nootovich.common.Nice;
import nootovich.nglib.NGFileSystem;
import nootovich.nglib.NGUtils;

import static nootovich.nice.NiceColor.*;

// NICE - Nootovich's Image Compression Encoding

public class Encoder {

    public static BitAccumulator niceImgData   = new BitAccumulator();
    public static NiceColor      prevColor     = new NiceColor(Color.BLACK);
    public static NiceColor      nextPrevColor = new NiceColor(Color.BLACK);

    public static void encode(String inputImage, String outputPath) {

        BufferedImage inputImg = null;
        try {
            inputImg = ImageIO.read(new File(inputImage));
        } catch (IOException e) {
            NGUtils.error(e.getMessage());
        }
        int w = inputImg.getWidth();
        int h = inputImg.getHeight();

        niceImgData.push("NICE"); // Header
        niceImgData.push(Nice.toBits(w, 16));
        niceImgData.push(Nice.toBits(h, 16));
        niceImgData.push(Nice.toBits(RED_BITDEPTH - 1, 3));
        niceImgData.push(Nice.toBits(GREEN_BITDEPTH - 1, 3));
        niceImgData.push(Nice.toBits(BLUE_BITDEPTH - 1, 3));

        int run = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color     sourceColor = new Color(inputImg.getRGB(x * inputImg.getWidth() / w, y * inputImg.getHeight() / h));
                NiceColor color       = new NiceColor(sourceColor);
                prevColor     = nextPrevColor;
                nextPrevColor = color;

                if (color.equals(prevColor)) {
                    run++;
                    continue;
                } else if (run > 0) {
                    if (run > Nice.TAG_MAX_DATA) {
                        niceImgData.push(Nice.TAG_RUN_CHUNK);
                        niceImgData.push(Nice.toBits((run / Nice.TAG_MAX_DATA) - 1, Nice.TAG_DATA_LEN));
                        run %= Nice.TAG_MAX_DATA;
                    }
                    if (run > 0) {
                        niceImgData.push(Nice.TAG_RUN);
                        niceImgData.push(Nice.toBits(run - 1, Nice.TAG_DATA_LEN));
                    }
                    run = 0;
                }

                boolean redClose   = NGUtils.margin(color.r, prevColor.r, 7, 8);
                boolean greenClose = NGUtils.margin(color.g, prevColor.g, 7, 8);
                boolean blueClose  = NGUtils.margin(color.b, prevColor.b, 3, 4);

                if (redClose && greenClose && blueClose) {
                    niceImgData.push(Nice.TAG_DIFF_SMALL);
                    niceImgData.push(Nice.toBits(color.r - prevColor.r + 8, 4));
                    niceImgData.push(Nice.toBits(color.g - prevColor.g + 8, 4));
                    niceImgData.push(Nice.toBits(color.b - prevColor.b + 4, 3));
                    continue;
                }

                niceImgData.push(Nice.TAG_COLOR);
                niceImgData.push(color.getBits());
            }
        }

        NGFileSystem.saveFile(outputPath, niceImgData.getData());
    }
}
