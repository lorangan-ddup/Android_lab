package com.example.lib;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Socket> mList = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        int port = 9999;

        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println(InetAddress.getLocalHost());


        Thread gameThread = new Thread(() -> {
            try {
                acceptConnections(serverSocket);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        gameThread.start();

    }

    private static void acceptConnections(ServerSocket serverSocket) throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("Waiting client connect");
            // 进行连接请求的接受
            Socket socket1 = serverSocket.accept();
            Socket socket2 = serverSocket.accept();
            mList.add(socket1);
            mList.add(socket2);
            System.out.println(mList.size());

            // 处理Socket连接
            new Thread(new OnlineSever(socket1,socket2)).start();
            new Thread(new OnlineSever(socket2,socket1)).start();
        }
    }

}
