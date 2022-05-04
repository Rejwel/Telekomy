package zad_3;

import java.util.List;
import java.util.Map;

public class Data {

    public static void changeToBytes(Map<Character, String> dictionary, String text, List<Byte> bytesToSend) {
        int counter = 7;
        byte byteToSend = 0;
        for (int i = 0; i < text.length(); i++) {
            char temp = text.charAt(i);
            String value = dictionary.get(temp);
            System.out.println(value);
            System.out.println(value.length());
            for (int j = 0; j < value.length(); j++) {
                if (counter == -1) {
                    bytesToSend.add(byteToSend);
                    byteToSend = 0;
                    counter = 7;
                }
                if (Integer.parseInt(String.valueOf(value.charAt(j))) == 1) {
                    byteToSend |= 1 << counter;
                }
                counter--;
            }
        }
        bytesToSend.add(byteToSend);
        byte amountOfZeros = (byte) ((byte) counter + 1);
        bytesToSend.add(amountOfZeros);
    }

    public static String changeToString(Map<String, Character> dictionary, List<Byte> deliveredBytes) {
        int lastByte = deliveredBytes.remove(deliveredBytes.size() - 1);
        int counter = 7;
        String value = "";
        String result = "";
        for (int i = 0; i < deliveredBytes.size(); i++) {
            byte temp = deliveredBytes.get(i);
            while (counter > -1) {
                if (i == deliveredBytes.size() - 1 && counter < lastByte) {
                    break;
                }
                System.out.println(value);
                value += ((temp >> counter) & 1);

                if (dictionary.containsKey(value)) {
                    result += dictionary.get(value);
                    value = "";
                }
                counter--;
            }
            counter = 7;
        }
        return result;
    }
}
