package com.example.lib;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyClass {
    public static void main(String[] args){
        new MyClass();
    }

    public MyClass(){
        try {
            // 1. 创建ServerSocket
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("--Listener Port: 9999--");
            while (true) {
                //2.等待接收请求，这里接收客户端的请求
                Socket client = serverSocket.accept();

                //3.开启子线程线程处理和客户端的消息传输
                new ServerSocketThread(client).start();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}