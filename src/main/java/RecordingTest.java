// Запись звуковых данных в файл

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

public class RecordingTest {
    // текущий звуковой файл
    private File file;
    // полное имя файла
    private String soundFileName;
    // основное имя файла
    private String filename = "samples_";
    // номер файла
    private int suffix = 0;
    // аудиформат
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private int MONO = 1;
    // определение формата аудиоданных
    private AudioFormat format = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,44100, 16, MONO, 2, 44100, true);
    // микрофонный вход
    private TargetDataLine mike;

    // создать новый файл
    File getNewFile() {
        try {
            do {
                // новое название файла
                soundFileName = filename + (suffix++) + "."+ fileType.getExtension();
                file = new File(soundFileName);
            } while (!file.createNewFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return file;
    }

    // запуск записи
    public void startRecording() {
        new Thread() {
            public void run() {
                // линию соединения
                DataLine.Info info = new DataLine.Info(TargetDataLine.class,format);
                // проверить, поддерживается ли линия
                if (!AudioSystem.isLineSupported(info)) {
                    JOptionPane.showMessageDialog(null, "Line not supported"
                                    + info, "Line not supported",
                            JOptionPane.ERROR_MESSAGE);
                }
                try {
                    // получить подходящую линию
                    mike = (TargetDataLine) AudioSystem.getLine(info);
                    // открываем линию соединения с указаным форматом и размером
                    // буфера
                    mike.open(format, mike.getBufferSize());
                    // поток микрофона
                    AudioInputStream sound = new AudioInputStream(mike);
                    // запустить линию соединения
                    mike.start();
                    // записать содержимое потока в файл
                    AudioSystem.write(sound, fileType, file);
                } catch (LineUnavailableException ex) {
                    JOptionPane.showMessageDialog(null, "Line not available"
                                    + ex, "Line not available",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "I/O Error " + ex,
                            "I/O Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.start();
    }

    // остановка записи
    public void stopRecording() {
        mike.stop();
        mike.close();
    }

    public static void main(String[] args) throws InterruptedException {
        RecordingTest rec = new RecordingTest();
        File f = rec.getNewFile();
        rec.startRecording();
        Thread.sleep(1000);
        rec.stopRecording();
    }
}

