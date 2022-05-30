package zad_3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Data {

    public static List<Byte> changeToBytes(Map<Character, String> dictionary, String text) {
        List<Byte> bytesToSend = new ArrayList<>(); // tworzenie listy
        int counter = 7; // tworzenie zmiennej pomocniczej
        byte byteToSend = 0; // tworzenie zmiennej pomocniczej
        for (int i = 0; i < text.length(); i++) { // petla po dlugosci tekstu
            char temp = text.charAt(i); //przypisanie wartosci chara z tekstu do zmiennej
            String value = dictionary.get(temp); // odczytanie kodu dla odpowiedniego chara
            for (int j = 0; j < value.length(); j++) {
                if (counter == -1) {
                    bytesToSend.add(byteToSend); //dodawanie bitu do listy
                    byteToSend = 0; // ustawianie bitu na 0
                    counter = 7; // ustawianie pozycji na 7
                }
                if (Integer.parseInt(String.valueOf(value.charAt(j))) == 1) { // sprawdzanie czy bit jest 1
                    byteToSend |= 1 << counter; //ustawianie bitu
                }
                counter--; // zmniejszanie zmiennej
            }
        }
        bytesToSend.add(byteToSend); // dodanie bitu do listy
        byte amountOfZeros = (byte) ((byte) counter + 1);
        bytesToSend.add(amountOfZeros);

        return bytesToSend; // zwrocenie listy bitow
    }

    public static String changeToString(Map<String, Character> dictionary, List<Byte> deliveredBytes) {
        int lastByte = deliveredBytes.remove(deliveredBytes.size() - 1); //pozycja ostatniego bitu
        int counter = 7; //ustawienie wartosci zmiennej na 7
        String value = ""; // zmienna value
        String result = ""; // zmienna wynik
        for (int i = 0; i < deliveredBytes.size(); i++) {
            byte temp = deliveredBytes.get(i); //przypisanie wartosci bitu
            while (counter > -1) {
                if (i == deliveredBytes.size() - 1 && counter < lastByte) { // przerwanie funkcji
                    break;
                }
                value += ((temp >> counter) & 1); //odczytanie wartosci szczegolnego bitu

                if (dictionary.containsKey(value)) {
                    result += dictionary.get(value); //dodanie wartosci litery do wynikowego stringa
                    value = "";
                }
                counter--; //zmniejszenie wartosci zmiennej
            }
            counter = 7; //ustawienie wartosc zmiennej na 7
        }
        return result;
    }
}
