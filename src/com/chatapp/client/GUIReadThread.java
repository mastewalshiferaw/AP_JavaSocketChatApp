package com.chatapp.client;

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class GUIReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClientGUI clientGUI;

    public GUIReadThread(Socket socket, ChatClientGUI clientGUI) {
        this.socket = socket;
        this.clientGUI = clientGUI;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            clientGUI.appendMessage("Error getting input stream: " + ex.getMessage());
            JOptionPane.showMessageDialog(
                    clientGUI,
                    "Error getting input stream: " + ex.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                if (response == null) {
                    clientGUI.appendMessage("Server has closed the connection.");
                    clientGUI.disconnectedFromServer();
                    break;
                }
                clientGUI.appendMessage(response);
            } catch (IOException ex) {
                if (!socket.isClosed()) {
                    clientGUI.appendMessage("Error reading from server: " + ex.getMessage());
                    JOptionPane.showMessageDialog(
                            clientGUI,
                            "Error reading from server: " + ex.getMessage(),
                            "Read Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                clientGUI.disconnectedFromServer();
                break;
            }
        }
    }
}
