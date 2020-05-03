import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class TestWaveFile {
    public static void main(String[] args) throws Exception {
      
        /*
        // создание одноканального wave-файла из массива целых чисел
        System.out.println("Создание моно-файла...");
        int[] samples = new int[3000000];
        for(int i=0; i < samples.length; i++){
            samples[i] = (int)Math.round((Integer.MAX_VALUE/2)*
                    (Math.sin(2*Math.PI*440*i*0.5/44100)));
          //  System.out.print(samples[i]+" ");
        }

       WaveFile wf = new WaveFile(4, 44100, 1, samples);
        wf.saveFile(new File("C:/Users/fvd/Desktop/testwav1.wav"));
        System.out.println("Продолжительность моно-файла: "+wf.getDurationTime()+ " сек.");
        */
WaveFile wf=new WaveFile();
wf.createAudioFile("C:/Users/fvd/Desktop/1.txt");
/*
        // Создание стерео-файла
        System.out.println("Создание стерео-файла...");
        int x=0;
        for(int i=0; i < samples.length; i++){
            samples[i++] = (int)Math.round((Integer.MAX_VALUE/2)*
                    (Math.sin(2*Math.PI*329.6*x/44100)));
            samples[i] = (int)Math.round((Integer.MAX_VALUE/2)*
                    (Math.sin(2*Math.PI*440*x/44100)));
            x++;
        }
        wf = new WaveFile(4, 44100, 2, samples);
        wf.saveFile(new File("C:/Users/fvd/Desktop/testwav2.wav"));
        System.out.println("Продолжительность стерео-файла: "+wf.getDurationTime()+ " сек.");

        // Чтение данных из файла
        System.out.println("Чтение данных из моно-файла:");
        wf = new WaveFile(new File("C:/Users/fvd/Desktop/testwav1.wav"));
        for(int i=0; i<10; i++){
            System.out.println(wf.getSampleInt(i));
        }*/
    }

}
