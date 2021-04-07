package test;

import java.io.*;

public class testFile {
    private static String pathname="test1";
    private static BufferedReader input;
    private static int[][] board = new int[9][9];

    static {
        try {
            input = new BufferedReader(new FileReader(new File(pathname)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        try {
            String line = null; //not declared within while loop
            int lineIndex = 0;
            while (( line = input.readLine()) != null) {
                System.out.println(line);
                for (int letterIndex = 0; letterIndex < 81; letterIndex++) {
                    final int value = Character.getNumericValue(line.charAt(letterIndex));
                    if(letterIndex % 9 == 0&& letterIndex!=0) {
                        lineIndex++;
                    }
                    board[lineIndex][letterIndex%9] = value;
//                    System.out.println(lineIndex);
                }
            }
            System.out.println(lineIndex);
        }
        finally {
            input.close();
        }
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }
}

