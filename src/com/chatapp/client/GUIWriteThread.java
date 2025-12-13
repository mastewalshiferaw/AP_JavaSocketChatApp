package com.chatapp.client;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class GUIWriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClientGUI clientGUI;

    public GUIWriteThread(Socket socket, ChatClientGUI clientGUI) {
        this.socket = socket;
        this.clientGUI = clientGUI;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            clientGUI.appendMessage("Error getting output stream: " + ex.getMessage());
            JOptionPane.showMessageDialog(
                    clientGUI,
                    "Error getting output stream: " + ex.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void run() {
        writer.println(clientGUI.getUserName());
    }

    public void send(String message) {
        if (socket.isConnected() && !socket.isClosed()) {
            writer.println(message);
        } else {
            clientGUI.appendMessage("Cannot send message: Not connected to server.");
        }
    }

    public void sendDisconnectMessage() {
        if (socket.isConnected() && !socket.isClosed()) {
            writer.println("bye");
        }
    }
}
