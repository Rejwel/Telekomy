package zad_3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Map<Character, String> map1 = new HashMap<>();
        Map<String, Character> map2 = new HashMap<>();
        Scanner s = new Scanner(System.in);

        BufferedReader bf = new BufferedReader(new FileReader("src/main/resources/dictionary.txt"));

        String line = "";

        while ((line = bf.readLine()) != null) {
            String[] temp = line.split(",");
            if (temp[0].equals("/")) {
                map1.put((char) 10, temp[1]);
                map2.put(temp[1], (char) 10);
                continue;
            }
            map1.put(temp[0].charAt(0), temp[1]);
            map2.put(temp[1], temp[0].charAt(0));
        }
        bf.close();

        BufferedReader file = new BufferedReader(new FileReader("src/main/resources/plik.txt"));
        line = "";
        String result = "";
        int counter = 0;
        while ((line = file.readLine()) != null) {
            if (counter > 0) {
                result += "\n";
            }
            result += line;
            counter++;
        }
        file.close();

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
                PortManager.getPort(s.nextInt(), Data.changeToBytes(map1, result));
            } else {
                PortManager.getPort(s.nextInt(), map2);
            }
            System.out.println("Komunikacja zakonczona");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}

