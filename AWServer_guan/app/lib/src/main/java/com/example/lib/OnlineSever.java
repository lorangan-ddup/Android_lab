package com.example.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class OnlineSever implements Runnable{

    Socket mysocket;
    Socket yoursocket;
    PrintWriter writer,ywriter;
    BufferedReader reader;
    String myUserName;
    String yourUserName;
    String content = "";

    public OnlineSever(Socket msocket,Socket ysocket) throws IOException {
        this.mysocket = msocket;
        this.yoursocket = ysocket;

        reader = new BufferedReader(new InputStreamReader(mysocket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(mysocket.getOutputStream(), StandardCharsets.UTF_8), true);
        ywriter = new PrintWriter(new OutputStreamWriter(yoursocket.getOutputStream(), StandardCharsets.UTF_8), true);

    }

    @Override
    public void run() {

        try {
            //房间中用户达到两个，则开始游戏
            if(Main.mList.size()==2){
                writer.println("start");
            }

            while((content = reader.readLine())!=null){
                if(content.equals("end")){
                    //游戏结束
                    Main.mList.remove(mysocket);
                    System.out.println(Main.mList.size());
                    if(Main.mList.size()==0){
                        break;
                    }
                }else{
                    ywriter.println(content);
                }
            }

            //房间中没有用户，则结束游戏
            if(Main.mList.size()==0){
                writer.println("gameover");
                System.out.println("gameover");
                ywriter.println("gameover");
                System.out.println("gameover");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}