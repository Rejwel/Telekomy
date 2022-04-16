package zad_2;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);

        InputStream inputStream = new FileInputStream("src/main/resources/cos.bmp");
        byte[] bytes = inputStream.readAllBytes();
        System.out.println(bytes.length);
        int kindOfPort;
        while (true) {
            System.out.println("Nadajnik czy odbiornik:");
            System.out.println("1. Nadajnik");
            System.out.println("2. Odbiornik");
            kindOfPort = s.nextInt();
            if (kindOfPort == 1 || kindOfPort == 2) {
                break;
            } else {
                System.out.println("Wybrano zla opcje");
            }
        }
        String[] portsList = PortManager.getPortsNameList();

        System.out.println("Wybierz port:");
        for (String port : portsList) {
            System.out.println(port);
        }
        System.out.print("Wybór: ");
        try {
            if (kindOfPort == 1) {
                PortManager.getPort(s.nextInt(), Data.divideBytesToMax128Byte(bytes));
            } else {
                PortManager.getPort(s.nextInt());
            }
            System.out.println("Komunikacja rozpoczęta");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
