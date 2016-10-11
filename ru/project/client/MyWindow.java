package ru.project.client;

import ru.project.server.ClientHandler;

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
import java.util.Vector;

/**
 * Created by FlameXander on 04.10.2016.
 */
public class MyWindow extends JFrame {
    Socket sock = null;
    DataInputStream in = null;
    DataOutputStream out = null;
    JTextField jtf = null;
    JTextArea jta = null;
    JPanel upperPanel;
    JPanel bottomPanel;
    boolean auth = false;

    public MyWindow() {
        setBounds(800, 300, 400, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jta = new JTextArea();
        jta.setEditable(false);
        JScrollPane jsp = new JScrollPane(jta);
        add(jsp);
        bottomPanel = new JPanel(new BorderLayout());
        JButton jbSend = new JButton("Send");
        jtf = new JTextField();
        bottomPanel.add(jbSend, BorderLayout.EAST);
        bottomPanel.add(jtf, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

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
        upperPanel = new JPanel(new GridLayout(1, 3));
        add(upperPanel, BorderLayout.NORTH);
        JTextField jtfLogin = new JTextField();
        JTextField jtfPass = new JTextField();
        JButton jbAuth = new JButton("Auth");
        upperPanel.add(jtfLogin);
        upperPanel.add(jtfPass);
        upperPanel.add(jbAuth);

        jbAuth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                sendAuthMsg(jtfLogin.getText(), jtfPass.getText());
            }
        });

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
        try {
            out.writeUTF("/auth " + login + " " + pass);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enableAuthPanel(boolean x) {
        if (x) {
            upperPanel.setVisible(true);
            bottomPanel.setVisible(false);
        } else {
            upperPanel.setVisible(false);
            bottomPanel.setVisible(true);
        }
    }

    public void connect() {
        if (sock == null) {
            try {
                //sock = new Socket("83.221.205.67", 8189);
                sock = new Socket("127.0.0.1", 8189);
                in = new DataInputStream(sock.getInputStream());
                out = new DataOutputStream(sock.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str != null && str.equals("abcd")) {
                            auth = true;
                            enableAuthPanel(false);
                            break;
                        }
                    }

                    while (true) {
                        String str = in.readUTF();
                        if (str != null) {
                            jta.append(str);
                            jta.append("\n");
                            jta.setCaretPosition(jta.getDocument().getLength());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void disconnect() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
