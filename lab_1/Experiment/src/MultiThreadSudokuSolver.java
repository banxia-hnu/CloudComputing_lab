import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadSudokuSolver {
    /**
     *
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {

        /**
         * 清空answer
         */
        File log = new File("answer");
        FileWriter fileWriter =new FileWriter(log);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        /**
         * 清空sortedAnswer
         */
        File log1 = new File("sortedAnswer");
        FileWriter fileWriter1 =new FileWriter(log1);
        fileWriter1.write("");
        fileWriter1.flush();
        fileWriter1.close();

        long startTime=System.nanoTime();
        File file=new File("test1");
        final BufferedReader input =  new BufferedReader(new FileReader(file));
        ExecutorService executorService= Executors.newCachedThreadPool();
        int i=1;
        try{
            String line = null;
            while((line=input.readLine())!=null){
                executorService.execute(new Task(line,i));
                i++;
            }
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        SortAnswer sortAnswer=new SortAnswer();
//        sortAnswer.sort();
        GetLineNumber getLineNumber=new GetLineNumber();
        while(true)
        {
            if(getLineNumber.getLineNumber(log)==getLineNumber.getLineNumber(file))
            {

                long endTime=System.nanoTime();
                System.out.println("时间" + (endTime-startTime)+"ns");
                SortAnswer sortAnswer=new SortAnswer();
                sortAnswer.sort();
                break;
            }
        }


    }
}

class Task implements Runnable{
    String string;
    int i;
    Task(String string,int i){
        this.string=string;
        this.i=i;
    }
    @Override
    public void run() {
        Thread.currentThread().setName(String.valueOf(i));
        SudokuSolver sudokuSolver=new SudokuSolver();
        sudokuSolver.sudokuSolver(string);
//        System.out.println(Thread.currentThread().getName());
    }
}
