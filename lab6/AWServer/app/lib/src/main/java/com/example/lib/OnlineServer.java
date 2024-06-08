package com.example.lib;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
public class OnlineServer implements Runnable{
    private Socket msocket;
    private Socket ysocket;

    public BufferedReader reader;
    public PrintWriter writer,ywriter;
    public String mname;
    public String yName;
    public String content;




    public OnlineServer(Socket msocket,Socket ysocket) throws IOException{
        this.msocket=msocket;
        this.ysocket=ysocket;

        reader=new BufferedReader(new InputStreamReader(msocket.getInputStream()));
        writer=new PrintWriter(new OutputStreamWriter(msocket.getOutputStream(),StandardCharsets.UTF_8),true);
        ywriter=new PrintWriter(new OutputStreamWriter(ysocket.getOutputStream(),StandardCharsets.UTF_8),true);
    }

    public void run(){

        try {
            if (Main.all_socket.size() == 2) {
                //游戏开始,发送给客户端
                writer.println("start");
            }

            while ((content = reader.readLine()) != null) {
                if (content.equals("end")) {
                    Main.all_socket.remove(msocket);
                    if(Main.all_socket.size()==0){
                        break;
                    }
                }
                else{
                    ywriter.println(content);
                }
            }

            if(Main.all_socket.size()==0){
                writer.println("gameover");
                ywriter.println("gameover");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
