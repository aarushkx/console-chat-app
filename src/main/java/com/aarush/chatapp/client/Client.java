package com.aarush.chatapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 1234;

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private InputHandler inputHandler;

    public static void main(String[] args) {
        Client client = new Client();
        new Thread(client).start();
    }

    @Override
    public void run() {
        try {
            client = new Socket(IP_ADDRESS, PORT);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            inputHandler = new InputHandler(in, out);
            new Thread(inputHandler).start();

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            System.err.println("Error in client connection: " + e);
        } finally {
            cleanUp();
        }
    }

    private void cleanUp() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null && !client.isClosed()) {
                client.close();
            }
            System.out.println("Client connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e);
        }
    }

}
