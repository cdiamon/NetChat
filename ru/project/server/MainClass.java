package ru.project.server;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class MainClass {
    public static void main(String[] args) {
        ServerSocket serv = null;
        try {
            serv = new ServerSocket(8189);
            System.out.println("Waiting for clients...");
            while(true) {
                Socket sock = serv.accept();
                System.out.println("Client connected");
                ClientHandler ch = new ClientHandler(sock);
                new Thread(ch).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serv.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
