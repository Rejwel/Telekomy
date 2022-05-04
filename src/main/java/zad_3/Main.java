package zad_3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        Map<Character, String> map1 = new HashMap<>();
        Map<String, Character> map2 = new HashMap<>();

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
            map2.put(temp[1],temp[0].charAt(0));
        }
        bf.close();
        String temp = "a\nla ma kota\ndzień dobry";
        List<Byte> list = new ArrayList<>();

        Data.changeToBytes(map1,temp,list);

        System.out.println(Data.changeToString(map2,list));

    }
}
