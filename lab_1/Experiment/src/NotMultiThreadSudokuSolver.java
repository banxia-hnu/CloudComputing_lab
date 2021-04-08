import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotMultiThreadSudokuSolver {
    public static void main(String[] args) throws IOException {
        /**
         * 清空answer
         */
        File log = new File("answer");
        FileWriter fileWriter = new FileWriter(log);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        /**
         * 清空sortedAnswer
         */
        File log1 = new File("sortedAnswer");
        FileWriter fileWriter1 = new FileWriter(log1);
        fileWriter1.write("");
        fileWriter1.flush();
        fileWriter1.close();

        long startTime = System.nanoTime();
        File file = new File("test100");
        final BufferedReader input = new BufferedReader(new FileReader(file));
        try {
            String line = null;
            while ((line = input.readLine()) != null) {
                SudokuSolver.sudokuSolver(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        SortAnswer sortAnswer=new SortAnswer();
//        sortAnswer.sort();
        long endTime = System.nanoTime();
        System.out.println("时间" + (endTime - startTime) + "ns");
    }
}
