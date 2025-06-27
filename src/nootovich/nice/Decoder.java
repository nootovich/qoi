package nootovich.nice;

import java.awt.Color;
import nootovich.common.BitAccumulator;
import nootovich.common.Nice;
import nootovich.nglib.*;

public class Decoder extends NGMain {

    public BitAccumulator niceImageData = new BitAccumulator();
    public Color[]        imgData;
    public Color          prevColor     = Color.BLACK;
    public NiceColor      prevNiceColor = new NiceColor(Color.BLACK);

    public void main(String niceImageFilepath) {
        niceImageData.push(NGFileSystem.loadBytes(niceImageFilepath));
        niceImageData.peekFirstByte();
        if (!niceImageData.pollString(4).equals("NICE")) NGUtils.error("Provided file is not a NICE image file.");
        w       = niceImageData.poll(16);
        h       = niceImageData.poll(16);
        imgData = new Color[w * h];
        int redBitDepth   = niceImageData.poll(3) + 1;
        int greenBitDepth = niceImageData.poll(3) + 1;
        int blueBitDepth  = niceImageData.poll(3) + 1;

        int i = 0;
        while (niceImageData.data.size() >= 8) {
            int tag = niceImageData.poll(Nice.TAG_LEN);
            if (tag == Nice.COLOR) {
                int r = niceImageData.poll(redBitDepth) << 8 - redBitDepth | (1 << 8 - redBitDepth) - 1;
                int g = niceImageData.poll(greenBitDepth) << 8 - greenBitDepth | (1 << 8 - greenBitDepth) - 1;
                int b = niceImageData.poll(blueBitDepth) << 8 - blueBitDepth | (1 << 8 - blueBitDepth) - 1;
                imgData[i] = new Color(r, g, b);
                prevColor  = imgData[i++];
                // System.out.println("Color: " + imgData[i - 1]);
            } else if (tag == Nice.RUN) {
                int run = niceImageData.poll(Nice.TAG_DATA_LEN) + 1;
                for (int j = 0; j < run; j++) imgData[i++] = new Color(prevColor.getRGB());
                // System.out.println("Run: " + run);
            } else if (tag == Nice.RUN_CHUNK) {
                int run = (niceImageData.poll(Nice.TAG_DATA_LEN) + 1) * Nice.TAG_MAX_DATA;
                for (int j = 0; j < run; j++) imgData[i++] = new Color(prevColor.getRGB());
                // System.out.println("Run: " + run);
            } else if (tag == Nice.DIFF_SMALL) {
                // TODO: unhardcode diff lengths
                int dr = niceImageData.poll(4) - 8 << 8 - redBitDepth;
                int dg = niceImageData.poll(4) - 8 << 8 - redBitDepth;
                int db = niceImageData.poll(3) - 4 << 8 - redBitDepth;
                int r  = prevColor.getRed() + dr;
                int g  = prevColor.getGreen() + dg;
                int b  = prevColor.getBlue() + db;
                if (r > 255 || g > 255 || b > 255) {
                    imgData[i] = new Color(0x59C26D);
                    // imgData[i] = new Color(0x9847CE);
                    System.out.printf("Overflow at [%d, %d]%n", i % 640, i / 640);
                } else if (r < 0 || g < 0 || b < 0) {
                    // imgData[i] = new Color(0x59C26D);
                    imgData[i] = new Color(0x9847CE);
                    System.out.printf("Underflow at [%d, %d]%n", i % 640, i / 640);
                } else {
                    imgData[i] = new Color(r, g, b);
                }
                System.out.printf("Diff: %s => %s%n", prevColor, imgData[i]);
                prevColor = imgData[i++];
            } else {
                NGUtils.error("Unreachable");
            }
        }

        setTickRate(1);
        setFrameRate(1);
        createWindow(w, h, new DecoderRenderer());
        start();
        renderImage(window.g);
    }

    public void renderImage(NGGraphics g) {
        g.drawRect(0, 0, w, h, Color.BLACK);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = imgData[y * w + x];
                if (c == null) g.drawPixel(x, y, (x / 8 + y / 8) % 2 == 0 ? Color.MAGENTA : Color.BLACK);
                else g.drawPixel(x, y, c);
            }
        }
    }
}
