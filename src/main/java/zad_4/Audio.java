package zad_4;

import javax.sound.sampled.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Audio {
    private AudioFormat audioFormat;
    private static TargetDataLine targetDataLine;
    private OutputStream outputStream;


    public static void main(String[] args) throws InterruptedException {
        Audio audio = new Audio();
        audio.captureAudio();
        Thread.sleep(5000);
        targetDataLine.stop();
        targetDataLine.close();
        playRecord();
    }

    public static void playRecord() {
        try {
            Clip clip = AudioSystem.getClip();
            //File record = new File("C:\\SEMESTR4\\TELE\\src\\main\\resources\\s8k.wav");
            File record = new File("record");
            clip.open(AudioSystem.getAudioInputStream(record));
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
        } catch (Exception exc) {
            System.out.println("error");
        }
    }

    class CaptureThread extends Thread {

        public void run() {
            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
            File audioFile = new File("record");
            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void captureAudio() {
        try {
            audioFormat = new AudioFormat(22000.0F, 16, 1, true, false);
            // sampleRate, sampleSizeInBits, channels, signed, bigEndian)
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            CaptureThread captureThread = new CaptureThread();
            captureThread.start();
        } catch (NullPointerException e) {
            System.out.println("error");
        } catch (IllegalArgumentException | LineUnavailableException e) {
            System.out.println("error");
            System.exit(0);
        }
    }
}
