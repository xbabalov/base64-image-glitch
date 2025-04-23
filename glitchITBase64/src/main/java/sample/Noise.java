package sample;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.concurrent.ThreadLocalRandom;

public class Noise {

    public static BufferedImage execute(BufferedImage img, int intensity) {
        BufferedImage noisedImg = deepCopy(img);

        graining(noisedImg, intensity);

        return noisedImg;
    }

    private static int randomInt(int boundary) {
        return ThreadLocalRandom.current().nextInt(0, boundary + 1);
    }

    private static Color lerp(Color pixelColor, Color randomColor, float mixPercentage) {
        float inversePercentage = 1 - mixPercentage;
        float red = pixelColor.getRed() * mixPercentage + randomColor.getRed() * inversePercentage;
        float green = pixelColor.getGreen() * mixPercentage + randomColor.getGreen() * inversePercentage;
        float blue = pixelColor.getBlue() * mixPercentage + randomColor.getBlue() * inversePercentage;
        return new Color(red / 255, green / 255, blue / 255);
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private static void graining(BufferedImage noisedImg, int intensity) {
        boolean previousPixelGlitched = false;
        Color randomColor = new Color(randomInt(255), randomInt(255), randomInt(255));

        for (int i = 0; i < intensity; i++) {
            for (int x = 0; x < noisedImg.getWidth(); x += randomInt(100)) {
                for (int y = 0; y < noisedImg.getHeight(); y += randomInt(100)) {
                    if (randomInt(100) < 25 || (previousPixelGlitched && randomInt(100) < 80)) {
                        previousPixelGlitched = true;
                        Color pixelColor = new Color(noisedImg.getRGB(x, y));
                        float mixPercentage = (float) (.5 + randomInt(50) / 100);
                        Color newColor = lerp(pixelColor, randomColor, mixPercentage);
                        noisedImg.setRGB(x, y, newColor.getRGB());
                    } else {
                        previousPixelGlitched = false;
                        randomColor = new Color(randomInt(255), randomInt(255), randomInt(255), 255);
                    }
                }
            }
        }
    }

    //not used but I'll still keep it here
    private static void stripeGlitching(BufferedImage noisedImg) {
        boolean previousPixelGlitched = false;
        Color randomColor = new Color(randomInt(255), randomInt(255), randomInt(255));

        for (int x = 0; x < noisedImg.getWidth(); x++) {
            for (int y = 0; y < noisedImg.getHeight(); y++) {
                if (randomInt(100) < 25 || (previousPixelGlitched && randomInt(100) < 80)) {
                    previousPixelGlitched = true;
                    Color pixelColor = new Color(noisedImg.getRGB(x, y));
                    float mixPercentage = (float) (.5 + randomInt(50) / 100);
                    Color newColor = lerp(pixelColor, randomColor, mixPercentage);
                    noisedImg.setRGB(x, y, newColor.getRGB());
                } else {
                    previousPixelGlitched = false;
                    randomColor = new Color(randomInt(255), randomInt(255), randomInt(255), 255);
                }
            }
        }
    }
}
