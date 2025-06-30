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
                prevNiceColor = new NiceColor(niceImageData.poll(redBitDepth), niceImageData.poll(greenBitDepth), niceImageData.poll(blueBitDepth));
                imgData[i]    = prevNiceColor.getColor();
                prevColor     = imgData[i++];
            } else if (tag == Nice.RUN) {
                int run = niceImageData.poll(Nice.TAG_DATA_LEN) + 1;
                for (int j = 0; j < run; j++) imgData[i++] = new Color(prevColor.getRGB());
            } else if (tag == Nice.RUN_CHUNK) {
                int run = (niceImageData.poll(Nice.TAG_DATA_LEN) + 1) * Nice.TAG_MAX_DATA;
                for (int j = 0; j < run; j++) imgData[i++] = new Color(prevColor.getRGB());
            } else if (tag == Nice.DIFF_SMALL) {
                // TODO: unhardcode diff lengths
                int dr = niceImageData.poll(4) - 8;
                int dg = niceImageData.poll(4) - 8;
                int db = niceImageData.poll(3) - 4;
                prevNiceColor = new NiceColor(prevNiceColor.r + dr, prevNiceColor.g + dg, prevNiceColor.b + db);
                imgData[i] = prevNiceColor.getColor();
                prevColor = imgData[i++];
            } else {
                NGUtils.error("Unreachable");
            }
        }

        setTickRate(1);
        setFrameRate(1);
        createWindow(w, h, new NGRenderer() {
            @Override
            public void render(NGGraphics g) {
                g.drawRect(0, 0, w, h, Color.BLACK);
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        Color c = imgData[y * w + x];
                        if (c == null) g.drawPixel(x, y, (x / 8 + y / 8) % 2 == 0 ? Color.MAGENTA : Color.BLACK);
                        else g.drawPixel(x, y, c);
                    }
                }
            }

            @Override
            public void reset() { }
        });
        createWindow(w, h, new NGRenderer() {
            @Override
            public void render(NGGraphics g) {
                g.drawRect(0, 0, w, h, Color.BLACK);
                g.drawImage(Encoder.inputImage, new NGVec2i(), new NGVec2i(w, h));
            }

            @Override
            public void reset() { }
        });
        int pad = (1920 - 2 * w) / 3;
        windows.getFirst().jf.setTitle("NICE");
        windows.getFirst().jf.setLocation(pad, (1080 - h) / 2);
        windows.getLast().jf.setTitle("Source");
        windows.getLast().jf.setLocation(1920 - w - pad, (1080 - h) / 2);
        start();
    }
}
