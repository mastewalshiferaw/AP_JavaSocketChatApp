package com.chatapp.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        if (client.getUserName() == null) {
            System.out.print("Enter your name: ");
            String userName = scanner.nextLine();
            client.setUserName(userName);
            writer.println(userName);
        } else {
            writer.println(client.getUserName());
        }

        String text;

        do {
            System.out.print("[" + client.getUserName() + "]: ");
            text = scanner.nextLine();
            writer.println(text);
        } while (!text.equalsIgnoreCase("bye"));

        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error closing socket in WriteThread: " + ex.getMessage());
        }

        scanner.close();
    }
}
