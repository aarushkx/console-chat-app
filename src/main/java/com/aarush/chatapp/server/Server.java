package com.aarush.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private static final int PORT = 1234;
    private ArrayList<ClientConnection> connections;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        isRunning = true;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            pool = Executors.newCachedThreadPool();

            System.out.println("Server is running on port " + PORT);

            while (isRunning) {
                Socket client = serverSocket.accept();
                ClientConnection clientConnection = new ClientConnection(client, this);
                connections.add(clientConnection);
                pool.execute(clientConnection);
            }
        } catch (IOException e) {
            System.err.println("Error setting up server socket: " + e);
            shutdown();
        }
    }

    public void broadcast(String message) {
        for (ClientConnection clientConnection : connections) {
            if (clientConnection != null) {
                clientConnection.sendMessage(message);
            }
        }
    }

    public void shutdown() {
        try {
            isRunning = false;

            for (ClientConnection clientConnection : connections) {
                clientConnection.cleanUp();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server has been shut down.");
        } catch (IOException e) {
            System.err.println("Error shutting down the server: " + e);
        }
    }

}
