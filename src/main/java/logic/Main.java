package logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) throws IOException {

        Transmission T = new Transmission();
        Scanner in = new Scanner(System.in);
        InputStream inputStream = new FileInputStream("src/main/resources/plik.txt");

        while(true) {
            System.out.println("Choose option:");
            System.out.println("1. Encode bits to clear bits from file");
            System.out.println("2. Encode bits to normal bytes from file");
            System.out.println("3. Repair bits from clear bits and create decoded file");
            System.out.println("0. Exit");
            char opt = in.next().charAt(0);
            switch (opt) {
                case '1' -> {
                    System.out.println("Processing...");
                    byte[] bytes = inputStream.readAllBytes();
                    int[][] bits = T.decodeAllBytes(bytes);
                    T.saveToClearBits(bits);
                    System.out.println("Done!");
                    System.out.println();
                }
                case '2' -> {
                    System.out.println("Processing...");
                    byte[] bytes = inputStream.readAllBytes();
                    int[][] bits = T.decodeAllBytes(bytes);
                    T.saveToEncodedBytes(bits);
                    System.out.println("Done!");
                    System.out.println();
                }
                case '3' -> {
                    System.out.println("Processing...");
                    int[][] bits = T.readFromClearBits();
                    T.repairAndSaveToFile(bits);
                    System.out.println("Done!");
                    System.out.println();
                }
                case '0' -> exit(0);
            }
        }
    }
}