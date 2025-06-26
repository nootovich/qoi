package nootovich.nice;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import nootovich.common.BitAccumulator;
import nootovich.nglib.NGFileSystem;

import static nootovich.nice.NiceColor.*;

// NICE - Nootovich's Image Compression Encoding

public class Encoder {
    public static BitAccumulator niceImgData = new BitAccumulator();

    public static void encode(String inputImage, String outputPath) throws IOException {

        BufferedImage inputImg = ImageIO.read(new File(inputImage));
        int           w        = inputImg.getWidth();
        int           h        = inputImg.getHeight();

        niceImgData.push("NICE"); // Header
        niceImgData.push(RED_BITDEPTH);
        niceImgData.push(GREEN_BITDEPTH);
        niceImgData.push(BLUE_BITDEPTH);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x += 3) {
                Color     sourceColor = new Color(inputImg.getRGB(x * inputImg.getWidth() / w, y * inputImg.getHeight() / h));
                NiceColor color       = new NiceColor(sourceColor);
                niceImgData.push(color.getBits());
            }
        }

        NGFileSystem.saveFile(outputPath, niceImgData.getData());
    }
}
