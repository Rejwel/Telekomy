package zad_2;


import java.util.ArrayList;
import java.util.List;

public class Data {

    public static byte[][] divideBytesToMax128Byte(byte[] bytes) {
        byte[][] converted;
        if (bytes.length % 128 == 0) {
            converted = new byte[bytes.length / 128][128];
        } else {
            converted = new byte[bytes.length / 128 + 1][128];
        }
        for (int i = 0; i < bytes.length; i++) {
            converted[i / 128][i % 128] = bytes[i];
        }
        int counter = bytes.length % 128;
        int amountOfZeros = 128 - counter - 1;
        for (int i = 0; i < amountOfZeros; i++) {
            converted[converted.length - 1][counter] = 0;
            counter++;
        }
        converted[converted.length - 1][127] = (byte) amountOfZeros;
        return converted;
    }

    public static byte[] countCheckSum(byte[] bytes) {
        byte[] result = new byte[2];
        int checksum = 0;
        for (byte aByte : bytes) {
            checksum += aByte;
        }
        result[0] = (byte) (checksum >>> 8);
        result[1] = (byte) checksum;
        return result;
    }
}
