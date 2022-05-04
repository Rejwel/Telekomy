package zad_1.logic;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transmission {

    private int[][] H = {
            {1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0},
            {1,1,0,1,1,1,1,1,0,1,0,0,0,0,0,0},
            {1,1,0,1,1,1,0,1,0,0,1,0,0,0,0,0},
            {1,1,0,1,0,1,0,1,0,0,0,1,0,0,0,0},
            {1,0,0,1,0,1,0,1,0,0,0,0,1,0,0,0},
            {1,0,0,1,0,1,0,0,0,0,0,0,0,1,0,0},
            {1,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}
    };

    public int[] getErrorTable(int[] code) {
        int[] out = new int[H.length];

        for (int i = 0; i < H.length; i++) {
            int temp = 0;
            for (int j = 0; j < code.length; j++) {
                temp += H[i][j] * code[j];
            }
            out[i] = temp%2;
        }

        return out;
    }

    public int[] encodeCharacter(int[] character) {
        int[] out = new int[16];
        int[] controlBits = getErrorTable(character);

        for (int i = 0; i < 8; i++) {
            out[i] = character[i];
            out[i+8] = controlBits[i];
        }

        return out;
    }

    public int[] getHE(int[] errorTable) {
        int[] out = new int[H.length];

        for (int i = 0; i < H.length; i++) {
            int temp = 0;
            for (int j = 0; j < errorTable.length; j++) {
                temp += H[i][j] * errorTable[j];
            }
            out[i] = temp%2;
        }

        return out;
    }

    public boolean isCorrect(int[] errorTable) {
        for (int i = 0; i < errorTable.length; i++) {
            if(errorTable[i] != 0) return false;
        }
        return true;
    }

    public int[] getAddedErrorHCols(int[] errorTable) {

        int[] fcol;
        int[] scol;

        for (int i = 0; i < H.length; i++) {
            fcol = getColFromColNumber(i);
            for (int j = 0; j < H.length; j++) {
                scol = getColFromColNumber(j);

                if(Arrays.equals(addTwoCols(fcol, scol), errorTable)) return new int[]{i,j};
            }
        }

        return new int[]{0,0};
    }

    public int getErrorHCol(int[] errorTable) {
        int[] fcol;

        for (int i = 0; i < H.length; i++) {
            fcol = getColFromColNumber(i);
            if(Arrays.equals(fcol, errorTable)) return i;
        }

        return -1;
    }

    public int[] getColFromColNumber(int colNumber) {
        int[] output = new int[8];

        for (int i = 0; i < H.length; i++) {
            output[i] = H[i][colNumber];
        }

        return output;
    }

    public int[] addTwoCols(int[] col1, int[] col2) {
        int[] output = new int[col1.length];

        for (int i = 0; i < col1.length; i++) {
            output[i] = (col1[i] + col2[i]) % 2;
        }

        return output;
    }

    public int[][] decodeAllBytes(byte[] bytes) {

        int[][] encodedBytes = new int[bytes.length][];

        for (int i = 0; i < bytes.length; i++) {
            StringBuilder bits = new StringBuilder(Integer.toBinaryString(bytes[i]));

            while(bits.length() < 8) {
                bits.insert(0, "0");
            }

            int[] bitsFromCharacter = new int[8];

            for (int j = 0; j < bits.length(); j++) {
                bitsFromCharacter[j] = bits.charAt(j) == 49 ? 1 : 0;
            }

            int[] outputBits;
            outputBits = Arrays.copyOf(bitsFromCharacter, 16);
            outputBits = encodeCharacter(outputBits);

            encodedBytes[i] = outputBits;
        }
        return encodedBytes;
    }

    public void saveToClearBits(int[][] bits) throws IOException {
        FileWriter fw = new FileWriter("src/main/resources/clearBits.bin");
        for (int i = 0; i < bits.length; i++) {
            for (int j = 0; j < bits[i].length; j++) {
                fw.write(String.valueOf(bits[i][j]));
            }
            if(bits.length - 1 != i)
                fw.write("\n");
        }
        fw.close();
    }

    public void repairAndSaveToFile(int[][] bits) throws IOException {

        for (int i = 0; i < bits.length; i++) {

            var errorTable = getErrorTable(bits[i]);

            // check if program has no errors
            if (isCorrect(errorTable)) {
                System.out.println("Byte " + i);
                System.out.println("There is no errors");
                System.out.println();
                continue;
            }
            // check if program has one error
            else if(getErrorHCol(errorTable) != -1) {
                System.out.println("Byte " + i);
                System.out.println("Error is on " + getErrorHCol(errorTable) + " bit");
                System.out.println("Repairing bits...");
                bits[i] = repairBits(bits[i], getErrorHCol(errorTable));
            }
            // check if program has 2 errors
            else if(!Arrays.equals(getAddedErrorHCols(errorTable), new int[]{0, 0})) {
                System.out.println("Byte " + i);
                System.out.println("Error is on " + getAddedErrorHCols(errorTable)[0] + " and " + getAddedErrorHCols(errorTable)[1] +  " bit");
                System.out.println("Repairing bits...");
                bits[i] = repairBits(bits[i], getAddedErrorHCols(errorTable));
            }
            // there is more errors or given code is not valid
            else{
                System.out.println("Byte " + i);
                System.out.println("There is more than 2 errors or given code is not valid");
                System.out.println();
                continue;
            }

            System.out.println();
        }

        saveToBytes(bits);
        saveToClearBits(bits);
    }

    public int[] repairBits(int[] bits, int errorPlace) {
        if (bits[errorPlace] == 1) bits[errorPlace] = 0;
        else if (bits[errorPlace] == 0) bits[errorPlace] = 1;
        return bits;
    }

    public int[] repairBits(int[] bits, int[] errorPlaces) {
        for (int i = 0; i < 2; i++) {
            if (bits[errorPlaces[i]] == 1) bits[errorPlaces[i]] = 0;
            else if (bits[errorPlaces[i]] == 0) bits[errorPlaces[i]] = 1;
        }
        return bits;
    }

    public void saveToEncodedBytes(int[][] bits) throws IOException {
        byte[] byteArray = new byte[bits.length*2];
        int bytePlace = 0;
        for (int i = 0; i < bits.length; i++) {
            int[] bitsToConvert = new int[8];
            int counter = 0;
            for (int j = 0; j < bits[i].length/2; j++) {
                bitsToConvert[counter] = bits[i][j];
                counter++;
            }
            byteArray[bytePlace++] = bitsToByte(bitsToConvert);
            counter = 0;
            for (int j = bits[i].length/2; j < bits[i].length; j++) {
                bitsToConvert[counter] = bits[i][j];
                counter++;
            }
            byteArray[bytePlace++] = bitsToByte(bitsToConvert);
        }
        FileUtils.writeByteArrayToFile(new File("src/main/resources/encodedFile.bin"), byteArray);
    }

    public void saveToBytes(int[][] bits) throws IOException {
        byte[] byteArray = new byte[bits.length];
        int bytePlace = 0;
        for (int i = 0; i < bits.length; i++) {
            int[] bitsToConvert = new int[8];
            int counter = 0;
            for (int j = 0; j < bits[i].length/2; j++) {
                bitsToConvert[counter] = bits[i][j];
                counter++;
            }
            byteArray[bytePlace++] = bitsToByte(bitsToConvert);
        }
//        FileUtils.writeStringToFile(new File("src/main/resources/decodedFile.bin"), new String(byteArray), StandardCharsets.UTF_8);
        FileUtils.writeByteArrayToFile(new File("src/main/resources/decodedFile.bin"), byteArray);
    }

    public int[][] readFromClearBits() throws IOException {
        String output = FileUtils.readFileToString(new File("src/main/resources/clearBits.bin"), StandardCharsets.UTF_8);

        Pattern pattern = Pattern.compile("\n");
        Matcher matcher = pattern.matcher(output);
        int rowCount = 1;
        while (matcher.find()) {
            rowCount++;
        }

        int[][] bitoutput = new int[rowCount][16];
        int counter = 0;

        for (int i = 0; i < bitoutput.length; i++) {
            for (int j = 0; j < 8; j++) {
                bitoutput[i][j] = output.charAt(counter++) == 49 ? 1 : 0;
            }
            for (int j = 8; j < 16; j++) {
                bitoutput[i][j] = output.charAt(counter++) == 49 ? 1 : 0;
            }
            if(bitoutput.length - 1 != i)
                counter++;
        }

        return bitoutput;
    }


    public byte bitsToByte(int[] bits) {
        int maxValue = 128;
        byte output = 0;
        for (int i = 0; i < bits.length; i++) {
            if(bits[i] == 1) output += maxValue;
            maxValue /= 2;
        }
        return output;
    }
}
