package logic;

import java.util.Arrays;

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
}
