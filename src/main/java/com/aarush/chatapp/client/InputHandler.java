package com.aarush.chatapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class InputHandler implements Runnable {

    private volatile boolean isRunning = false;
    private BufferedReader in;
    private PrintWriter out;

    public InputHandler(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
        this.isRunning = true;
    }

    @Override
    public void run() {
        try {
            String message;
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            while (isRunning) {
                message = consoleInput.readLine();
                if (message != null) {
                    if (message.equals("/exit")) {
                        System.out.println("Exiting the chat...");
                        consoleInput.close();
                        shutdown();
                        break;
                    } else {
                        out.println(message);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        isRunning = false;
        try {
            System.exit(0);
            in.close();
            out.close();
            System.out.println("Client shut down!");
        } catch (IOException e) {
            System.err.println("Error while shutting down: " + e);
        }
    }

}
