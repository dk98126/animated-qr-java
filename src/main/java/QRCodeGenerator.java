import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class QRCodeGenerator {
    private static final String QR_CODE_IMAGE_PATH = "qr/MyQRCode.gif";
    private static final int MAX_BYTES_PER_SINGLE_QR_FOR_PAYLOAD = 248;
    private static final int SINGLE_META_INFO_SIZE = 4;

    private static void generateQRCodeImage(String data, int width, int height, String filePath)
            throws WriterException, IOException {
        List<BufferedImage> bufferedImages = getBufferedImages(data, width, height);
        writeGif(filePath, bufferedImages, 100, true);
    }

    private static void writeGif(String filePath, List<BufferedImage> bufferedImages, int timeBetweenFramesMS, boolean loopContinuously) throws IOException {
        if (!bufferedImages.isEmpty()) {
            ImageOutputStream outputStream = new FileImageOutputStream(new File(filePath));
            GifSequenceWriter writer = new GifSequenceWriter(outputStream, bufferedImages.get(0).getType(), timeBetweenFramesMS, loopContinuously);
            for (BufferedImage bufferedImage : bufferedImages) {
                writer.writeToSequence(bufferedImage);
            }
            writer.close();
            outputStream.close();
        }
    }

    private static List<BufferedImage> getBufferedImages(String data, int width, int height) throws WriterException {
        byte[] bytes = data.getBytes();
        int length = bytes.length;
        int divider = MAX_BYTES_PER_SINGLE_QR_FOR_PAYLOAD;
        int quotient = length / divider;
        int residue = length % divider;
        int imagesAmount = quotient + (residue == 0 ? 0 : 1);
        List<BufferedImage> bufferedImages = new ArrayList<>();
        int i;
        for (i = 0; i < quotient; i++) {
            int payloadStart = i * divider;
            int payloadEnd = (i * divider + divider);
            byte[] payloadWithInfo = formPayloadWithMetaInfo(bytes, imagesAmount, i + 1, payloadStart, payloadEnd);
            String tmp = new String(payloadWithInfo, StandardCharsets.UTF_8);
            bufferedImages.add(getBufferedImage(tmp, width, height));
        }
        if (residue != 0) {
            int payloadStart = i * divider;
            int payloadEnd = (i * divider + residue);
            byte[] payloadWithInfo = formPayloadWithMetaInfo(bytes, imagesAmount, i + 1, payloadStart, payloadEnd);
            String tmp = new String(payloadWithInfo, StandardCharsets.UTF_8);
            bufferedImages.add(getBufferedImage(tmp, width, height));
        }
        return bufferedImages;
    }

    private static byte[] formPayloadWithMetaInfo(byte[] bytes, int imagesAmount, int imageOrder, int payloadStart, int payloadEnd) {
        byte[] payload = Arrays.copyOfRange(bytes, payloadStart, payloadEnd);
        ByteBuffer bb = ByteBuffer.allocate(SINGLE_META_INFO_SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(imageOrder);
        byte[] info = Arrays.copyOf(bb.array(), bb.array().length);
        bb.clear();
        bb.putInt(imagesAmount);
        info = ArrayUtils.addAll(info, Arrays.copyOf(bb.array(), bb.array().length));
        return ArrayUtils.addAll(info, payload);
    }

    private static BufferedImage getBufferedImage(String data, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    public static void main(String[] args) {
        try {
            File file = new File(ClassLoader.getSystemClassLoader().getResource("testimage.jpg").toURI());
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
            String stringToWrite = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            generateQRCodeImage(stringToWrite, 1000, 1000, QR_CODE_IMAGE_PATH);
        } catch (WriterException | IOException | URISyntaxException e) {
            log.error("Could not generate QR code");
            e.printStackTrace();
        }
    }
}
