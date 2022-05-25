package zad_4;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, LineUnavailableException {
        Scanner scanner = new Scanner(System.in);
        int value;
        int choice;
        System.out.println("Czestotliwosc: ");
        value = scanner.nextInt();
        System.out.println("1.Nadajnik");
        System.out.println("2.Odbiornik");
        choice = scanner.nextInt();
        switch (choice) {
            case 1:
                Sender sender = new Sender();
                AudioFormat format = new AudioFormat(value, 8, 1, true, false);
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
                break;
            case 2:
                scanner.nextLine();
                System.out.println("Podaj adres");
                String address = scanner.nextLine();
                Receiver receiver = new Receiver(address, value);
                break;
            default:
        }
    }
}
