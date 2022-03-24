package logic;

public class Transmission {

    int[][] H = {
                {1, 1, 0, 1, 1, 0, 0},
                {1, 1, 0, 1, 1, 0, 0},
                {1, 1, 0, 1, 1, 0, 0}
            };

    int[] code = {0, 0, 0, 1, 1, 1, 1};

    int[] op() {
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

    boolean isCorrect(int[] tab) {
        for (int i = 0; i < tab.length; i++) {
            if(tab[i] != 0) return false;
        }
        return true;
    }
}
