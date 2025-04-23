package sample;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;

class Glitch {

    static BufferedImage execute(File file, int iterations, int errors) {
        try {
            BufferedImage image = ImageIO.read(file);
            String imageString = imageToString(image);
            return corrupt(imageString, iterations, errors);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String imageToString(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Failed";
    }

    private static BufferedImage corrupt(String imageString, int iterations, int maxErrors) {
        int margin = 2200;

        String corrupted = imageString;
        for (int i = 0; i <= iterations; i++) {
            if (Math.random() > 0.7) {
                float errors = Math.round(Math.random() * maxErrors);
                for (int j = 0; j < errors; j++) {
                    float point = margin + Math.round(Math.random() * (corrupted.length() - margin - 1));
                    corrupted = corrupted.substring(0, Math.round(point)) + corrupted.charAt(Math.round(point + 1)) + corrupted.charAt(Math.round(point)) + corrupted.substring(Math.round(point + 2));
                }
            }
        }
        return stringToImage(corrupted);
    }

    private static BufferedImage stringToImage(String imageString) {
        byte[] bytes = Base64.getDecoder().decode(imageString);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            return ImageIO.read(bis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
