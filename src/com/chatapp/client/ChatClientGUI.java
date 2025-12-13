package com.chatapp.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClientGUI extends JFrame {
    private String hostname;
    private int port;
    private String userName;

    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private JTextField userNameField;
    private JButton connectButton;
    private JPanel connectPanel;

    private Socket socket;
    private GUIReadThread readThread;
    private GUIWriteThread writeThread;

    public ChatClientGUI(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        setTitle("Java Chat Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectPanel = new JPanel(new FlowLayout());
        userNameField = new JTextField(15);
        userNameField.setText("Guest");
        connectButton = new JButton("Connect");
        connectPanel.add(new JLabel("Your Name:"));
        connectPanel.add(userNameField);
        connectPanel.add(connectButton);
        add(connectPanel, BorderLayout.NORTH);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.setEnabled(false);
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        setVisible(true);
    }

    private void connectToServer() {
        userName = userNameField.getText().trim();
        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            socket = new Socket(hostname, port);
            messageArea.append("Connected to the chat server as " + userName + "\n");

            userNameField.setEnabled(false);
            connectButton.setEnabled(false);
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            messageField.requestFocusInWindow();

            readThread = new GUIReadThread(socket, this);
            readThread.start();
            writeThread = new GUIWriteThread(socket, this);
            writeThread.start();

        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(this, "Server not found: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "I/O Error: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        writeThread.send(text);
        messageField.setText("");
        messageField.requestFocusInWindow();
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    public String getUserName() {
        return userName;
    }

    public void disconnectedFromServer() {
        SwingUtilities.invokeLater(() -> {
            messageArea.append("Disconnected from the server.\n");
            userNameField.setEnabled(true);
            connectButton.setEnabled(true);
            messageField.setEnabled(false);
            sendButton.setEnabled(false);
            try {
                if (socket != null) socket.close();
            } catch (IOException ex) {
                System.err.println("Error closing socket: " + ex.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Syntax: java ChatClientGUI <hostname> <port-number>");
            System.exit(0);
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        SwingUtilities.invokeLater(() -> new ChatClientGUI(hostname, port));
    }
}
