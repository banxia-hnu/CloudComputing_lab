import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class httpserver {
    // 端口号
    private static  int PORT=8888;

    // 获取处理器核数
    private static  int COUNT=Runtime.getRuntime().availableProcessors();

    // 处理的任务量和线程数量、CPU、内存等资源都相关
    // 一般推荐使用跟处理器核数数量相等的线程数


    public static void main(String[] args) throws IOException {
        if(args.length>=6){
            PORT=Integer.valueOf(args[3]);
            COUNT=Integer.valueOf(args[5]);
        }
        final ExecutorService EXE =
                Executors.newFixedThreadPool(COUNT);
        ServerSocket server = new ServerSocket(PORT);
        // 一个 socket 对象代表一个客户端
        while (true) {
            // 获取客户端请求 socket 对象：阻塞方法
            Socket socket = server.accept();
            EXE.submit(new httptask(socket));
        }
    }
}

class httptask implements Runnable {

    private Socket socket;

    // 构造方法
    public httptask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // 获取客户端请求数据：输入流
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        // 获取客户端输出流，返回响应数据
        OutputStream os = null;
        PrintWriter pw = null;

        try{
            try{
                is=socket.getInputStream();
                // 响应输出数据
                // 将原生的字节流转换成字符流，方便处理
                isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                // 转换流放在缓冲流中读取效率比较高
                br = new BufferedReader(isr);
                Request request = new Request();
                // 请求数据的解析：http协议报的解包
                // 1.解析请求行(第一行): Method URL Version
                String requestLine = br.readLine();
                // 根据 " " 把请求行分成三部分
                String[] requestLines = requestLine.split(" ");
                // 添加 method
                request.setMethod(requestLines[0]);
                // 可能为 http://localhost:9999/xxx?uername=stu&passord=123
                String url = requestLines[1];
                if (url.contains("?")) {
                    String parameters = url.substring(url.indexOf("?") + 1);
                    request.parseParamaters(parameters);
                    url = url.substring(0, url.indexOf("?"));
                }
                // 当 method 为 get 时可能需要解析请求正文
                // 添加 url
                request.setUrl(url);
                // 添加 version
                request.setVersion(requestLines[2]);
                // 2.解析请求头
                // key : value 每个换行，以空行作为结尾
                String header;
                while ((header = br.readLine()) != null && header.length() != 0) {
                    // 第一个条件保证请求头还没有结束，
                    // 第二个条件保证读取到的元素不是空行
                    String key = header.substring(0, header.indexOf(":"));
                    String value = header.substring(header.indexOf(":") + 1);
                    request.addHeader(key, value.trim());
                }
                os = socket.getOutputStream();
                pw = new PrintWriter(os, true);

                if(request.getMethod().equals("GET")){
                    System.out.println(request);
                    if(request.getUrl().equals("/")){
                        InputStream htmlIs = httpserver.class.getClassLoader().getResourceAsStream("./index.html");
                        if (htmlIs != null) {
                            pw.println("HTTP/1.1 200 OK");
                            pw.println("Content-Type: text/html; charset=utf-8");
                            pw.println();
                            // 返回 webapp 下的静态资源文件内容
                            InputStreamReader htmlIsr = new InputStreamReader(htmlIs);
                            BufferedReader htmlBr = new BufferedReader(htmlIsr);
                            String content;
                            while ((content = htmlBr.readLine()) != null) {
                                pw.println(content);
                            }
                        } else {
                            // 返回 404
                            pw.println("HTTP/1.1 404 Not Found");
                            pw.println("Content-Type: text/html; charset=utf-8");
                            pw.println();
                            pw.println("<h2>Not Found</h2>");
                        }
                    }
                    else if(request.getUrl().contains("html")){
                        InputStream htmlIs = httpserver.class.getClassLoader().getResourceAsStream("."+request.getUrl());
                        if (htmlIs != null) {
                            pw.println("HTTP/1.1 200 OK");
                            pw.println("Content-Type: text/html; charset=utf-8");
                            pw.println();
                            // 返回 webapp 下的静态资源文件内容
                            InputStreamReader htmlIsr = new InputStreamReader(htmlIs);
                            BufferedReader htmlBr = new BufferedReader(htmlIsr);
                            String content;
                            while ((content = htmlBr.readLine()) != null) {
                                pw.println(content);
                            }
                        } else {
                            // 返回 404
                            pw.println("HTTP/1.1 404 Not Found");
                            pw.println("Content-Type: text/html; charset=utf-8");
                            pw.println();
                            pw.println("<h2>Not Found</h2>");
                        }
                    }
                    else{
                        InputStream htmlIs = httpserver.class.getClassLoader().getResourceAsStream("."+request.getUrl());
                        if (htmlIs != null) {
                            String s=request.getUrl();
                            s=s.substring(1);
                            pw.println("HTTP/1.1 200 OK");
                            pw.println("Content-Type: text/html; charset=utf-8");
                            pw.println();
                            // 返回 webapp 下的静态资源文件内容
//                            pw.println("<img src="+"\""+s+"\"" + ">");
                            pw.println("<img src=\"/images/2.jpg\"/>");
                        } else {
                            // 返回 404
                            pw.println("HTTP/1.1 404 Not Found");
                            pw.println("Content-Type: text/html; charset=utf-8");
                            pw.println();
                            pw.println("<h2>Not Found</h2>");
                        }
                    }
                }
                else if(request.getMethod().equals("POST")){
                    System.out.println(request);
                    if(request.getUrl().equals("/Post_show")) {
                        String len = request.getHeader("Content-Length");
                        if (len != null) {
                            // 将 String 类型转换为 int 类型
                            int l = Integer.parseInt(len);
                            char[] chars = new char[l];
                            br.read(chars, 0, l);
                            // 请求参数格式：Name=stu&ID=123
                            String s = new String(chars);
                            request.parseParamaters(s);
                        }
                        HashMap<String,String> hashMap= (HashMap<String, String>) request.getParameters();
                        ArrayList<String> arrayList=new ArrayList<>();
                        for(String key:hashMap.keySet()){
                            arrayList.add(key);
                        }
                        if((arrayList.get(0).equals("Name")&&arrayList.get(1).equals("ID"))||(arrayList.get(0).equals("ID")&&arrayList.get(1).equals("Name"))){
                            pw.println("HTTP/1.1 200 OK");
                            pw.println("<!DOCTYPE html>\n" +
                                    "<html lang=\"en\">\n" +
                                    "<head>\n" +
                                    "    <meta charset=\"UTF-8\">\n" +
                                    "    <title>POST method</title>\n" +
                                    "</head>\n" +
                                    "<body>\n" +
                                    "    Your Name:HNU\n" +
                                    "    ID:CS06142\n" +
                                    "</body>\n" +
                                    "</html>");
                        }
                        else{
                            pw.println("HTTP/1.1 404 Not Found");
                            pw.println("Content-Type: text/html; charset=utf-8");
                            pw.println();
                            pw.println("<!DOCTYPE html>\n" +
                                    "<html lang=\"en\">\n" +
                                    "<head>\n" +
                                    "    <meta charset=\"UTF-8\">\n" +
                                    "    <title>POST method</title>\n" +
                                    "</head>\n" +
                                    "<body>\n" +
                                    "    <h1>Not Found</h1>\n" +
                                    "</body>\n" +
                                    "</html>");
                        }
                    }
                    else{
                        pw.println("HTTP/1.1 404 Not Found");
                        pw.println("Content-Type: text/html; charset=utf-8");
                        pw.println();
                        pw.println("<!DOCTYPE html>\n" +
                                "<html lang=\"en\">\n" +
                                "<head>\n" +
                                "    <meta charset=\"UTF-8\">\n" +
                                "    <title>POST method</title>\n" +
                                "</head>\n" +
                                "<body>\n" +
                                "    <h1>Not Found</h1>\n" +
                                "</body>\n" +
                                "</html>");
                    }
                }
                else{
                    System.out.println(request);
                    pw.println("HTTP/1.1 501 Not Implemented");
                    pw.println("Content-Type: text/html; charset=utf-8");
                    pw.println();
                    pw.println("<h2>The Method Is Not Implemented</h2>");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 关闭流:反向关闭
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
                if (pw != null) {
                    pw.close();
                }
                if (os != null) {
                    os.close();
                }
                // 关闭客户端连接(短连接)
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

