package com.aarush.chatapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private String name;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    private Server server;
    private MessageHandler messageHandler;

    public ClientConnection(Socket client, Server server) {
        this.client = client;
        this.server = server;
        this.messageHandler = new MessageHandler(this, server);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out.println(
                    """
                            CHAT APP
                            Welcome! 🎉 We're excited to have you here! 🙌
                            What’s your name so we can get this convo started? 😎
                            """
            );

            name = in.readLine();
            if (!messageHandler.isValidName(name)) {
                out.println("Invalid name. Disconnecting...");
                System.out.println("Invalid name received. Disconnecting client.");
                cleanUp();
                return;
            }

            System.out.println(name + " connected!");
            server.broadcast(name + " joined the chat!");

            out.println("Type `/help` to see all the commands. 🔥");

            String message;
            while ((message = in.readLine()) != null) {
                messageHandler.handleMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Error with client connection: " + e);
        } finally {
            cleanUp();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void cleanUp() {
        try {
            in.close();
            out.close();
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e);
        }
    }

}
