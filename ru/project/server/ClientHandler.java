package ru.project.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by FlameXander on 04.10.2016.
 */
public class ClientHandler implements Runnable {
    private Socket sock;
    private MyServer owner;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public ClientHandler(MyServer owner, Socket sock) {
        this.owner = owner;
        this.sock = sock;
        this.nick = "";
        try {
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String str = in.readUTF();
                if (str != null && str.startsWith("/auth")) { // /auth login1 pass1
                    String login = str.split(" ")[1];
                    String pass = str.split(" ")[2];
                    String user = SQLHandler.getNickByLoginPass(login, pass);
                    if (user != null) {
                        nick = user;
                        sendMsg("abcd");
                        break;
                    } else sendMsg("Auth error");
                }
            }
            while (true) {
                String str = in.readUTF();
                if (str != null) {
                    if (str.equals("/end")) break;
                    System.out.println(nick + ": " + str);
                    owner.broadcastMsg(nick, str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                owner.broadcastMsg("server", "Client disconnected");
                owner.unsubscribe(this);
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
