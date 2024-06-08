package com.example.lib;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ServerSocketThread extends Thread{
    private BufferedReader in;
    private PrintWriter pw;
    private Socket socket;
    public ServerSocketThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);

            String content;

            while ((content = in.readLine()) != null) {
                //4.和客户端通信

            }
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }
}
