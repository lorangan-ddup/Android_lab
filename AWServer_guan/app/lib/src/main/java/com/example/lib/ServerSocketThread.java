package com.example.lib;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerSocketThread extends Thread{
    private Socket socket;
    private Socket opponentSocket;
    private PrintWriter pw;
    private PrintWriter opponentPw;

    public ServerSocketThread(Socket socket, Socket opponentSocket) {
        this.socket = socket;
        this.opponentSocket = opponentSocket;
        try {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            opponentPw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(opponentSocket.getOutputStream(), "UTF-8")), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
            String content;
            while ((content = in.readLine()) != null) {
                System.out.println("client(" + opponentSocket.getPort() + ") Received: " + content);
                if (content.equals("DISCONNECT")) {
                    handleDisconnect();
                    break;
                } else if (content.equals("EMPTY")){
                    handleEmpty();
                    break;
                } else if (content.equals("ALL_OVER")){
                    sendToOpponent(content+"+1");
                } else if (content.equals("ALL_OVER+1")){
                    socket.close();
                    opponentSocket.close();
                }else
                    sendToOpponent(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        pw.println(message);
        System.out.println("Sent to client(" + socket.getPort() + "): " + message);
    }

    private void sendToOpponent(String message) {
        if (opponentSocket != null && !opponentSocket.isClosed()) {
            opponentPw.println(message);
        }
    }

    private void handleDisconnect() {
        System.out.println("Client disconnected: " + socket);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (opponentSocket != null && !opponentSocket.isClosed()) {
            try {
                opponentPw = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(opponentSocket.getOutputStream(), "UTF-8")), true);
                opponentPw.println("OPPONENT_DISCONNECTED");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleEmpty() {
        System.out.println("Client Empty: " + socket);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (opponentSocket != null && !opponentSocket.isClosed()) {
            try {
                opponentPw = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(opponentSocket.getOutputStream(), "UTF-8")), true);
                opponentPw.println("OPPONENT_DISCONNECTED");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
