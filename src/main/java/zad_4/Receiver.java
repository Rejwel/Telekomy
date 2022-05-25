package zad_4;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class Receiver {
    private Socket socket;
    private byte[] bytes = new byte[4];

    public Receiver(String address) {
        try {
            while (socket == null) {
                socket = new Socket(address, 5000);
            }
            receivedData();
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void receivedData() throws IOException, LineUnavailableException {
        AudioFormat format = new AudioFormat(22000, 8, 1,  true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open();
        sourceLine.start();
        InputStream socketIn = socket.getInputStream();
        while (true) {
            bytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                bytes[i] = (byte) socketIn.read();
            }
            sourceLine.write(bytes, 0, 4);
        }
    }
}
