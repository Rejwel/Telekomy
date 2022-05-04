package zad_2;


import java.util.ArrayList;
import java.util.List;

public class Data {

    public static byte[][] divideBytesToMax128Byte(byte[] bytes) { //zmiana tablicy 1d na 2d ktora kazda ma 128 bajtow
        byte[][] converted;
        if (bytes.length % 128 == 0) {
            converted = new byte[bytes.length / 128][128]; //tworzenie tablicy
        } else {
            converted = new byte[bytes.length / 128 + 1][128]; //tworzenie tablicy
        }
        for (int i = 0; i < bytes.length; i++) {
            converted[i / 128][i % 128] = bytes[i]; //uzupelnianie tablic
        }
        int counter = bytes.length % 128;
        int amountOfZeros = 128 - counter - 1;
        for (int i = 0; i < amountOfZeros; i++) {
            converted[converted.length - 1][counter] = 0; //dodawanie zer do ostatniej miejsca tablicy
            counter++;
        }
        converted[converted.length - 1][127] = (byte) amountOfZeros; //zapisywanie liczby dodanych zer
        return converted;
    }

    public static byte[] countCheckSum(byte[] bytes) {// liczenie checksumy sumujac wartosc bajtow i dzielac przez 2^16
        byte[] result = new byte[2]; //tworzenie tablicy checksumy
        int checksum = 0; //inicjowanie checksumy
        for (byte aByte : bytes) {
            checksum += aByte; //dodawanie wartosci sumy
        }
        result[0] = (byte) (checksum >>> 8); //przypisywanie wartosci inta do bajtow
        result[1] = (byte) checksum;
        return result;
    }

    public static byte[] crc16Sum(byte[] bytes) { //liczenie crc16
        int crc = 0x0000;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ bytes[(crc ^ b) & 0xff];
        }
        byte[] result = new byte[2];
        result[0] = (byte) (crc >>> 8);
        result[1] = (byte) crc;
        return result;
    }

    /*
    *    100100111010
----------------
1010010100110000 : 10110    //bierzemy 5 pierwszych bitów dzielnej, pierwszy bit to 1, więc dopisujemy 1 do wyniku i pod spodem zapisujemy dzielnik
10110                       //wykonujemy odejmowanie czyli XOR
-----
 00101                      //dopisujemy kolejną cyfrę z dzielnej na koniec, najwyższy bit to 0, więc dopisujemy 0 do wyniku i pod spodem zapisujemy 0
 00000                      //wykonujemy XOR
 -----
  01010                     //to samo co poprzednio
  00000
  -----
   10101                    //pierwszy bit to 1, więc dopisujemy 1 do wyniku i pod spodem zapisujemy dzielnik
   10110                    //wykonujemy XOR
   -----
    00110                   //Powtarzamy aż dojdziemy do ostatniego bitu dzielnej
    00000
    -----
     01100
     00000
     -----
      11001
      10110
      -----
       11111
       10110
       -----
        10010
        10110
        -----
         01000
         00000
         -----
          10000
          10110
          -----
           01100
           00000
           -----
            1100            //Reszta po dojściu do ostatniej cyfry dzielnej to nasza suma CRC dla zadanych danych wejściowych i wielomianu.
    * */


}
