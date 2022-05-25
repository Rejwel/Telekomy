package zad_4;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, LineUnavailableException {

        Sender sender = new Sender();
//        Receiver receiver = new Receiver("169.254.59.39");
        AudioFormat format = new AudioFormat(22000, 8, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
        targetLine.open();
        Thread monitorThread = new Thread() {
            @Override
            public void run() {
                targetLine.start();

                byte[] data = new byte[targetLine.getBufferSize() / 5];
                System.out.println(targetLine.getBufferSize());

                while (true) {
                    targetLine.read(data, 0, data.length);
                    try {
                        sender.sendBytes(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        monitorThread.start();
    }


}
