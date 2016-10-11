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
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Socket sock) {
        this.sock = sock;
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
                if (str != null) {
                    out.writeUTF("echo: " + str);
                    out.flush();
                    if (str.equals("/end")) break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
