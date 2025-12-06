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
            ex.printStackTrace();
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();
        client.setUserName(userName);
        writer.println(userName);

        String text;

        do {
            System.out.print("[" + userName + "]: ");
            text = scanner.nextLine();
            writer.println(text);

        } while (!text.equalsIgnoreCase("bye"));

        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error writing to server: " + ex.getMessage());
        }
        scanner.close();
    }
}