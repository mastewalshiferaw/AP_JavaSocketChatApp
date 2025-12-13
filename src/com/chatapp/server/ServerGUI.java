package com.chatapp.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServerGUI extends JFrame {
    private JTextArea logArea;
    private JButton startButton;
    private JTextField portField;

    public ServerGUI() {
        super("Chat Server");

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Port:"));
        portField = new JTextField("5050", 5);
        startButton = new JButton("Start Server");
        topPanel.add(portField);
        topPanel.add(startButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
    }

    private void startServer() {
        String portStr = portField.getText();
        int port = Integer.parseInt(portStr);

        startButton.setEnabled(false);
        portField.setEnabled(false);
        appendLog("Attempting to start server on port " + port + "...\n");

        redirectSystemStreams();

        new Thread(() -> {
            try {
                ChatServer.main(new String[]{String.valueOf(port)});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void appendLog(String text) {
        SwingUtilities.invokeLater(() -> logArea.append(text));
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                appendLog(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendLog(new String(b, off, len));
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
