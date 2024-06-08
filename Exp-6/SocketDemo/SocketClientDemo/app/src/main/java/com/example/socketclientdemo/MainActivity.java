package com.example.socketclientdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    private Socket socket;
    private PrintWriter writer;
    private Handler handler;
    private EditText txt;
    private static  final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnConn = findViewById(R.id.btnConn);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnDiscon = findViewById(R.id.btnDiscon);

        btnConn.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnDiscon.setOnClickListener(this);
        txt = (EditText) findViewById(R.id.textView);

        handler = new Handler(Looper.getMainLooper()){
            //当数据处理子线程更新数据后发送消息给UI线程，UI线程更新UI
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 1){
                    txt.setText(msg.obj.toString());
                }
            }
        };
    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.btnConn){
            NetConn netConn = new NetConn(handler);
            netConn.start();;
        }
        if(view.getId() == R.id.btnSend){
            new Thread(){
                @Override
                public void run(){
                    Log.i(TAG, "send message to server");
                        writer.println("hello,server");
                }
            }.start();
        }
        if(view.getId() == R.id.btnDiscon) {
            new Thread(){
                @Override
                public void run(){
                    Log.i(TAG,"disconnect to server");
                    writer.println("bye");
                }
            }.start();
        }
    }

    protected class NetConn extends Thread{
        private BufferedReader in;
        private Handler toClientHandler;

        public NetConn(Handler myHandler){
            this.toClientHandler = myHandler;
        }
        @Override
        public void run(){
            try{
                //创建socket对象
                socket = new Socket();
                //connect,要保证服务器已启动
                socket.connect(new InetSocketAddress
                        ("10.0.2.2",9999),5000);

                //获取socket输入输出流
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
                writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(),"utf-8")),true);
                Log.i(TAG,"connect to server");

                //接收服务器返回的数据
                Thread receiveServerMsg =  new Thread(){
                    @Override
                    public void run(){
                        String fromserver = null;
                        try{
                            while((fromserver = in.readLine())!=null)
                            {
                                //发送消息给UI线程
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = fromserver;
                                toClientHandler.sendMessage(msg);
                            }
                        }catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                };
                receiveServerMsg.start();
            }catch(UnknownHostException ex){
                ex.printStackTrace();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
}

