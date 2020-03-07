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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            BufferedImage bufferedImage = webcam.getImage();
            Map<DecodeHintType, Object> hintsMap = new EnumMap<>(DecodeHintType.class);
            hintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hintsMap.put(DecodeHintType.POSSIBLE_FORMATS, List.of(BarcodeFormat.QR_CODE));

            QRCodeReader qrCodeReader = new QRCodeReader();
            LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));
            Result result = null;
            try {
                result = qrCodeReader.decode(binaryBitmap, hintsMap);
            } catch (NotFoundException | ChecksumException | FormatException ex) {
                ex.printStackTrace();
            }
            String text = Objects.requireNonNull(result).getText();
            textArea.append(text + "\n");
            BufferedImage miniBufferedImage = resize(bufferedImage, (int) (bufferedImage.getWidth() * 0.2), (int) (bufferedImage.getHeight() * 0.2));
            label.setIcon(new ImageIcon(miniBufferedImage));
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

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
