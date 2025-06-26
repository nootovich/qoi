package nootovich.testing.bitdepth;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import nootovich.nglib.NGMain;
import nootovich.nglib.NGUtils;

public class BitDepth extends NGMain {

    public static int           bitDepthR = 8;
    public static int           bitDepthG = 8;
    public static int           bitDepthB = 8;
    public static BufferedImage img;
    public static Color[][]     imgData;

    public void main(String imgFilepath) {
        try {
            img = ImageIO.read(new File(imgFilepath));
        } catch (IOException e) {
            NGUtils.error(e.getMessage());
        }

        setTickRate(1);
        setFrameRate(10);
        createWindow(img.getWidth(), img.getHeight(), new BitDepthRenderer());

        imgData = new Color[h][w];
        updateImgBitDepth();

        start();
    }

    public void redraw() {
        window.g.drawRect(0,0,w,h,Color.BLACK);
        for (int y = 0; y < imgData.length; y++) {
            for (int x = 0; x < imgData[0].length; x++) {
                window.g.drawRect(x, y, 1, 1, imgData[y][x]);
            }
        }
        window.g.drawRect(0, 0, 24, 12, Color.WHITE);
        window.g.drawText(String.valueOf(bitDepthR), 0, 10, Color.RED);
        window.g.drawText(String.valueOf(bitDepthG), 8, 10, Color.GREEN);
        window.g.drawText(String.valueOf(bitDepthB), 16, 10, Color.BLUE);
    }

    public void updateImgBitDepth() {
        for (int y = 0; y < imgData.length; y++) {
            for (int x = 0; x < imgData[0].length; x++) {
                Color pixel = new Color(img.getRGB(x, y), true);
                int   r     = (pixel.getRed() >> (8 - bitDepthR) << (8 - bitDepthR));// + (255 >> bitDepthR);
                int   g     = (pixel.getGreen() >> (8 - bitDepthG) << (8 - bitDepthG));// + (255 >> bitDepthG);
                int   b     = (pixel.getBlue() >> (8 - bitDepthB) << (8 - bitDepthB));// + (255 >> bitDepthB);
                imgData[y][x] = new Color(r, g, b, pixel.getAlpha());
            }
        }
        redraw();
    }

    @Override
    public void onQPress() {
        bitDepthR = NGUtils.clamp(bitDepthR + 1, 1, 8);
        updateImgBitDepth();
    }

    @Override
    public void onAPress() {
        bitDepthR = NGUtils.clamp(bitDepthR - 1, 1, 8);
        updateImgBitDepth();
    }

    @Override
    public void onWPress() {
        bitDepthG = NGUtils.clamp(bitDepthG + 1, 1, 8);
        updateImgBitDepth();
    }

    @Override
    public void onSPress() {
        bitDepthG = NGUtils.clamp(bitDepthG - 1, 1, 8);
        updateImgBitDepth();
    }

    @Override
    public void onEPress() {
        bitDepthB = NGUtils.clamp(bitDepthB + 1, 1, 8);
        updateImgBitDepth();
    }

    @Override
    public void onDPress() {
        bitDepthB = NGUtils.clamp(bitDepthB - 1, 1, 8);
        updateImgBitDepth();
    }
}
