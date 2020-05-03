import com.google.zxing.WriterException;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

public class AsciiGifGenerator {
    public static void main(String[] args) throws IOException, URISyntaxException, WriterException {
        Img2Ascii imageToAscii = new Img2Ascii();
        File file = new File(ClassLoader.getSystemClassLoader().getResource("testimage.jpg").toURI());
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        List<BufferedImage> bufferedQRImages = qrCodeGenerator.getBufferedImages(is.readAllBytes(), 100, 100);
        BufferedWriter writer = new BufferedWriter(new FileWriter("asciiart.txt"));
        for (BufferedImage image : bufferedQRImages) {
            writer.write(imageToAscii.convertToAscii(image, 300, 100));
            writer.write('\f');
        }
        writer.flush();
        writer.close();
    }
}
