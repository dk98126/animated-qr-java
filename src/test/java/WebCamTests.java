import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.Stopwatch;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class WebCamTests {
    @Test
    public void testCam() {
        Webcam webcam = Webcam.getDefault();
        Assert.assertNotNull("Не удалось обнаружить камеру", webcam);
        log.info("Камера обнаружена");
        webcam.close();
    }

    @Test
    public void takeAPictureAndSaveToFileTest() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage image = webcam.getImage();
        ImageIO.write(image, "PNG", new File("/tmp/test.png"));
        webcam.close();
    }
}
