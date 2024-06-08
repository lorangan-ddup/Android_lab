package com.example.lib;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class Main {
    public static List<Socket> all_socket=new ArrayList<>();
    private static int port=9999;
    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket=new ServerSocket(port);
        while(true){
            Socket socket1=serverSocket.accept();
            all_socket.add(socket1);
            Socket socket2=serverSocket.accept();
            all_socket.add(socket2);

            new Thread(new OnlineServer(socket1,socket2)).start();
            new Thread(new OnlineServer(socket2,socket1)).start();
        }
    }
}
