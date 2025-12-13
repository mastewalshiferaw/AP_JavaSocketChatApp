package com.chatapp.client;

import java.io.*;
import java.net.*;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;

    public ReadThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                if (response == null) {
                    System.out.println("\nDisconnected from the server.");
                    break;
                }
                System.out.println("\n" + response);
                if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }
            } catch (IOException ex) {
                if (!socket.isClosed()) {
                    System.out.println("Error reading from server: " + ex.getMessage());
                }
                break;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket in ReadThread: " + e.getMessage());
        }
    }
}
