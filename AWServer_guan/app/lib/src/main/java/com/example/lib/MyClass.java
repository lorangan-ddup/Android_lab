package com.example.lib;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;

public class MyClass {
    private static final int PORT = 9999;
    private static final int MAX_CLIENTS = 10;
    private static ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);
    public static void main(String[] args){
        new MyClass().startServer();
    }

    public void startServer(){
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("--Listener Port: 9999--");
            while (true) {
                Socket client1 = serverSocket.accept();
                System.out.println("client 1 connected:" + client1);

                Socket client2 = serverSocket.accept();
                System.out.println("client 2 connected:" + client2);

                ServerSocketThread client1Service = new ServerSocketThread(client1, client2);
                ServerSocketThread client2Service = new ServerSocketThread(client2, client1);

                executorService.execute(client1Service);
                executorService.execute(client2Service);

                client1Service.sendMessage("START_GAME");
                client2Service.sendMessage("START_GAME");

                System.out.println("Game started between Client 1 and Client 2");

                while (client1.isClosed() && client2.isClosed()) {
                    try {
                        Thread.sleep(1000); // Adjust sleep duration as needed
                        System.out.println("Wait for new clients");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        } finally {
            shutdownExecutorService();
        }
    }

    public static void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.err.println("Executor service did not terminate");
                }
                else
                    System.out.println("Executor service was terminated");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}