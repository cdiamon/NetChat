package ru.project.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by FlameXander on 07.10.2016.
 */
public class MyServer {
    private ServerSocket serv = null;
    private final int PORT = 8189;
    private ArrayList<ClientHandler> list;

    public MyServer() {
        try {
            list = new ArrayList<>();
            serv = new ServerSocket(PORT);
            SQLHandler.connect();
            System.out.println("Waiting for clients...");
            while(true) {
                Socket sock = serv.accept();
                System.out.println("Client connected");
                broadcastMsg("Server", "New client connected...");
                ClientHandler ch = new ClientHandler(this, sock);
                list.add(ch);
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

    public synchronized void broadcastMsg(String nick, String msg) {
        for(ClientHandler o : list) {
            o.sendMsg(nick + ": " + msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        list.remove(o);
    }
}
