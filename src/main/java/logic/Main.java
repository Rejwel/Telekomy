package logic;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        Transmission T = new Transmission();

        int[] code = {0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0};
//        int[] encodedCode = T.encodeCharacter(code);

        var errorTable = T.getErrorTable(code);

        // check if program has no errors
        if (T.isCorrect(errorTable)) {
            System.out.println("There is no errors");
        }
        // check if program has one error
        else if(T.getErrorHCol(errorTable) != -1) {
            System.out.println("Error is on " + T.getErrorHCol(errorTable) + " bit");
        }
        // check if program has 2 errors
        else if(!Arrays.equals(T.getAddedErrorHCols(errorTable), new int[]{0, 0})) {
            System.out.println("Error is on " + T.getAddedErrorHCols(errorTable)[0] + " and " + T.getAddedErrorHCols(errorTable)[1] +  " bit");
        }
        // there is more errors or given code is not valid
        else{
            System.out.println("There is more than 2 errors or given code is not valid");
        }

        System.out.println("stop");
    }
}