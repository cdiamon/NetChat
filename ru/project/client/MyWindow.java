package ru.project.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by FlameXander on 04.10.2016.
 */
public class MyWindow extends JFrame {
    Socket sock = null;
    DataInputStream in = null;
    DataOutputStream out = null;
    JTextField jtf = null;
    JTextArea jta = null;

    public MyWindow() {
        setBounds(800, 300, 400, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jta = new JTextArea();
        jta.setEditable(false);

        JScrollPane jsp = new JScrollPane(jta);
        add(jsp);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton jbSend = new JButton("Send");
        jtf  = new JTextField();
        bottomPanel.add(jbSend, BorderLayout.EAST);
        bottomPanel.add(jtf, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        connect();

        jbSend.addActionListener(e -> sendMsg());
        jtf.addActionListener(e -> sendMsg());
        jtf.requestFocus();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                disconnect();
            }
        });

        bottomPanel.setVisible(false);
        JPanel upperPanel = new JPanel(new GridLayout(1,3));
        add(upperPanel, BorderLayout.NORTH);
        JTextField jtfLogin = new JTextField();


        setVisible(true);
    }

    public void sendMsg() {
        String str = jtf.getText();
        jtf.setText("");
        jtf.requestFocus();
        try {
            out.writeUTF(str);
            out.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection error...");
            e.printStackTrace();
        }
    }

    public void sendAuthMsg(String login, String pass) {
        // /auth login1 pass1
        sendMsg();
    }

    public void connect() {
        try {
            sock = new Socket("83.221.205.67", 8189);
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                while (true) {
                    String str = in.readUTF();
                    if(str != null) {
                        jta.append(str);
                        jta.append("\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void disconnect() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
