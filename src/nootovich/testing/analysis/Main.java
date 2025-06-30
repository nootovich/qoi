package nootovich.testing.analysis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import nootovich.nglib.*;

public class Main {

    public static long[] values = new long[256];
    public static float  max    = 0;

    public static void main(String[] args) {
        traverseDir("./assets/jpg");
        System.out.println(Arrays.toString(values));
        values[255] = 0;
        for (long val: values) max = Math.max(val, max);
        new NGMain() {
            public void main() {
                setTickRate(1);
                setFrameRate(1);
                createWindow(512, 256, new NGRenderer() {
                    @Override
                    public void render(NGGraphics g) {
                        g.drawRect(0, 0, w, h, Color.BLACK);
                        for (int i = 0; i < values.length; i++) {
                            int rh = (int) (values[i] / max * 256);
                            g.drawRect(i * 2, h - rh, 2, rh, Color.WHITE);
                        }
                    }

                    @Override
                    public void reset() { }
                });
                start();
            }
        }.main();
    }

    public static void traverseDir(String path) {
        String[] filepaths = new File(path).list();
        for (String filepath: filepaths) {
            File file = new File(path + "/" + filepath);
            if (file.isDirectory()) {
                traverseDir(file.getPath());
                continue;
            }
            try {
                BufferedImage image = ImageIO.read(file);
                if (image == null) continue;
                int[] colors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
                for (int i = 0; i < colors.length; i++) {
                    values[colors[i] >> 24 & 0xFF]++;
                    values[colors[i] >> 16 & 0xFF]++;
                    values[colors[i] >> 8 & 0xFF]++;
                    values[colors[i] & 0xFF]++;
                }
            } catch (IOException e) { NGUtils.info("Couldn't open file " + file.getPath()); }
            // byte[] bytes = NGFileSystem.loadBytes(file.getPath());
            // for (byte value: bytes) values[value & 0xFF]++;
        }
    }
}
