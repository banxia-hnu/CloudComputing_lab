import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SortAnswer {
    public static void main(String[] args) throws IOException {
        sort();
    }
   public static void sort() throws IOException {
       HashMap<Integer,String> hashMap=new HashMap<Integer, String>();
       BufferedWriter writer = new BufferedWriter(new FileWriter("sortedAnswer",true));
       File file=new File("answer");
       final BufferedReader input =  new BufferedReader(new FileReader(file));
       try{
           String line = null;
           while((line=input.readLine())!=null){
               hashMap.put(Integer.valueOf(line.substring(81)),line.substring(0,81));
           }
           Set set=hashMap.keySet();
           Object[] arr=set.toArray();
           Arrays.sort(arr);
           for(Object key:arr){
//               System.out.println(key);
               writer.write(hashMap.get(key));
               writer.newLine();
               writer.flush();
           }
           writer.close();
       } catch (IOException e) {
           e.printStackTrace();
       }

   }
}


