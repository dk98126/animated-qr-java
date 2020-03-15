import java.awt.*;
import java.awt.image.BufferedImage;

public class Img2Ascii {
    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public String convertToAscii(BufferedImage img, int width, int height) {
        img = resize(img, width, height);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color pixelColor = new Color(img.getRGB(j, i));
                double pixelValue = (((pixelColor.getRed() * 0.30) + (pixelColor.getBlue() * 0.59) + (pixelColor
                        .getGreen() * 0.11)));
                stringBuilder.append(strChar(pixelValue));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    private char strChar(double g) {
        if (g <= 60) {
            return ' ';
        } else if (g <= 80) {
            return '.';
        } else if (g <= 110) {
            return '*';
        } else if (g <= 120) {
            return '+';
        } else if (g <= 170) {
            return '^';
        } else if (g <= 190) {
            return '&';
        } else if (g <= 210) {
            return '8';
        } else if (g <= 240) {
            return '#';
        } else {
            return '@';
        }
    }
}