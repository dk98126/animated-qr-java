import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class QRCodeReaderRecognizer {
    public static void main(String[] args) {

        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setDisplayDebugInfo(true);
        webcamPanel.setImageSizeDisplayed(true);
        webcamPanel.setMirrored(true);

        JTextArea textArea = new JTextArea();
        JButton readQrButton = new JButton();
        JLabel label = new JLabel();
        readQrButton.addActionListener(e -> {
            Map<DecodeHintType, Object> hintsMap = getQrHintMap();
            Map<Integer, byte[]> gifParts = new HashMap<>();
            int gifPartsNumber = -1;
            while (gifParts.size() != gifPartsNumber) {
                BufferedImage bufferedImage = webcam.getImage();
                QRCodeReader qrCodeReader = new QRCodeReader();
                LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
                Result result = null;
                try {
                    result = qrCodeReader.decode(binaryBitmap, hintsMap);
                } catch (NotFoundException | ChecksumException | FormatException ex) {
                    ex.printStackTrace();
                }
                if (result == null) {
                    continue;
                }
                String text = result.getText();
                textArea.append(text + "\n");
                BufferedImage miniBufferedImage = resize(bufferedImage, (int) (bufferedImage.getWidth() * 0.2), (int) (bufferedImage.getHeight() * 0.2));
                label.setIcon(new ImageIcon(miniBufferedImage));
                byte[] data = Base64.getDecoder().decode(text);
                int partN = ByteBuffer
                        .wrap(Arrays.copyOf(data, 4))
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getInt();
                if (gifPartsNumber == -1) {
                    gifPartsNumber = ByteBuffer
                            .wrap(Arrays.copyOfRange(data, 4, 8))
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .getInt();
                }
                System.out.println("Считана часть №" + partN + ", всего: " + gifPartsNumber + " осталось: " + (gifPartsNumber - gifParts.size()));
                gifParts.put(partN, data);
            }
            File file = new File("gifRecognizedFile");
            try {
                BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                List<Integer> keys = new ArrayList<>(gifParts.keySet());
                keys.sort(Integer::compareTo);
                List<byte[]> values = keys
                        .stream()
                        .map(key -> Arrays.copyOfRange(gifParts.get(key), 8, gifParts.get(key).length))
                        .collect(Collectors.toList());
                for (byte[] x : values) {
                    os.write(x);
                }
                os.flush();
                os.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Весь файл был успешно считан!");
            textArea.append("Весь файл был успешно считан!");
        });

        readQrButton.setText("Прочитать QR code");
        JFrame window = new JFrame("Test webcam panel");
        JPanel buttonsPanel = new JPanel();
        JSplitPane splitPane = new JSplitPane();
        buttonsPanel.setLayout(new BoxLayout(window, BoxLayout.Y_AXIS));
        window.add(webcamPanel);
        window.setResizable(true);
        window.getContentPane().setLayout(new GridLayout());
        window.getContentPane().add(splitPane);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setTopComponent(webcamPanel);
        splitPane.setBottomComponent(buttonsPanel);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(readQrButton);
        buttonsPanel.add(textArea);
        buttonsPanel.add(label);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
    }

    private static Map<DecodeHintType, Object> getQrHintMap() {
        Map<DecodeHintType, Object> hintsMap = new EnumMap<>(DecodeHintType.class);
        hintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hintsMap.put(DecodeHintType.POSSIBLE_FORMATS, List.of(BarcodeFormat.QR_CODE));
        return hintsMap;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
