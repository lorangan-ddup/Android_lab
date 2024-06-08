package com.example.lib;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class MyServer {
    private static final String TAG = "MyServer";
    private String content = "";
    public static void main(String args[]){
        new MyServer();
    }
    public  MyServer(){
        try{
            //获取本机IP地址
            InetAddress addr = InetAddress.getLocalHost();
            System.out.println("local host:" + addr);

            //创建server socket
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("listen port 9999");

            //等待客户端连接
            while(true){
                System.out.println("waiting client connect");
                //从连接请求队列中取出一个客户的连接请求，然后创建与客户连接的Socket对象，并将它返回
                // 如果队列中没有连接请求，accept()方法就会一直等待，直到接收到了连接请求才返回
                Socket socket = serverSocket.accept();
                //连接成功，返回socket对象
                System.out.println("accept client connect" + socket);
                new Thread(new Service(socket)).start();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class Service implements Runnable{
        private Socket socket;
        private BufferedReader in = null;

        public Service(Socket socket){
            this.socket = socket;
            try{
                //InputStreamReader把字节流转化成字符流
                //BufferedReader用于读取字符流。它继承自Reader类，提供了按行读取文件的功能
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            }catch (IOException ex){
                ex.printStackTrace();
            }
        }

        //传输数据过程在子线程中完成
        @Override
        public void run() {
            System.out.println("wait client message " );
            try {
                while ((content = in.readLine()) != null) {
                    //从socket连接读取到bye标识客户端发出断开连接请求
                    if(content.equals("bye")){
                        System.out.println("disconnect from client,close socket");
                        //关闭socket输入输出流
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();   //关闭socket连接
                        break;
                    }else {
                        //从socket连接读取到的不是断开连接请求，则像客户端发信息
                        this.sendMessge(socket);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        public void sendMessge(Socket socket) {
            PrintWriter pout = null;
            try{
                String message = "hello,client!";
                System.out.println("messge to client:" + message);

                //OutputStreamWriter：将字符流转换为字节流
                //BufferedWriter：是缓冲字符输出流
                //PrintWriter：字符类型的打印输出流
                pout = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(),"utf-8")),true);
                //利用输出流输出数据
                pout.println(message);

            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}