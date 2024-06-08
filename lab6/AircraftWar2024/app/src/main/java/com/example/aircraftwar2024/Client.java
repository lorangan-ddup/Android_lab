package com.example.aircraftwar2024;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Handler handler;
    private OnGameEventListener listener;
    private String serverIp;
    private int port;
    public interface OnGameEventListener {

        void onConnectSuccess();
        void onOpponentScoreUpdate(int score);
        void onGameOver();
        void onServerMessage();
        void onAllOver(int playerScore, int opponentScore);
        void setPlayerScore(int score);
        void setOpponentScore(int score);
        int getPlayerScore();
        int getOpponentScore();
    }

    public Client(OnGameEventListener listener) {
        this.listener = listener;
    }

    public void connectToServer(String serverIp, int port) {
        new Thread(() -> {
            try {
                this.serverIp = serverIp;
                this.port = port;
                socket = new Socket(serverIp, port);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8")), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    if ("MATCH_SUCCESS".equals(serverMessage)) {
                        listener.onConnectSuccess();
                    }
                    else if ("START_GAME".equals(serverMessage)) {
                        listener.onServerMessage();
                    } else if (serverMessage.startsWith("SCORE:")) {
                        int score = Integer.parseInt(serverMessage.substring(6));
                        listener.onOpponentScoreUpdate(score);
                        listener.setOpponentScore(score);
                    } else if ("GAME_OVER".equals(serverMessage)) {
                        listener.onGameOver();
                    } else if ("ALL_OVER+1".equals(serverMessage)){
                        listener.onAllOver(listener.getPlayerScore(),listener.getOpponentScore());
                    }
                }
                Log.i("Server+++","connect to server");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String message) {
        if (out != null) {
            Log.i("/////",message);
            out.println(message);
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.println("DISCONNECT");
                socket.close();
                socket = new Socket(serverIp, port);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8")), true);
                out.println("EMPTY");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}