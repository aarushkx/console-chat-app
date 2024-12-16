package com.aarush.chatapp.server;

public class MessageHandler {

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 20;
    private static final int MIN_MESSAGE_LENGTH = 1;
    private static final int MAX_MESSAGE_LENGTH = 500;

    private ClientConnection clientConnection;
    private Server server;

    public MessageHandler(ClientConnection clientConnection, Server server) {
        this.clientConnection = clientConnection;
        this.server = server;
    }

    public void handleMessage(String message) {
        try {
            switch (getMessageType(message)) {
                case NAME_CHANGE -> handleNameChange(message);
                case EXIT -> handleExit();
                case HELP -> handleHelp();
                default -> handleNormalMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Error while processing message: " + e);
            clientConnection.sendMessage("Sorry, there was an error processing your message.");
        }
    }

    private MessageType getMessageType(String message) {
        if (message.startsWith("/name ")) {
            return MessageType.NAME_CHANGE;
        } else if (message.startsWith("/exit")) {
            return MessageType.EXIT;
        } else if (message.startsWith("/help")) {
            return MessageType.HELP;
        } else {
            return MessageType.NORMAL;
        }
    }

    private void handleNameChange(String message) {
        String newName = message.substring(6).trim(); // Get name after "/name "

        if (isValidName(newName)) {
            String oldName = clientConnection.getName();
            clientConnection.setName(newName); // Set the new valid name
            clientConnection.sendMessage("Your name has been changed to: " + newName);
            server.broadcast(oldName + " changed their name to " + newName); // Broadcast to other clients
        } else {
            clientConnection.sendMessage("Invalid name. It must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters and cannot contain multiple words.");
        }
    }

    private void handleExit() {
        clientConnection.sendMessage("You have left the chat. Goodbye! 👋");
        server.broadcast(clientConnection.getName() + " has left the chat.");
        clientConnection.cleanUp();
    }

    private void handleHelp() {
        String helpMessage = """
                
                Here are the available commands:
                /name <new_name>  - Change your display name.
                /exit             - Leave the chat.
                /help             - Show this help message.
                
                """;
        clientConnection.sendMessage(helpMessage);
    }

    private void handleNormalMessage(String message) {
        if (isValidMessage(message)) {
            server.broadcast(clientConnection.getName() + ": " + message);
        } else {
            clientConnection.sendMessage("Message too long! It must be between " + MIN_MESSAGE_LENGTH + " and " + MAX_MESSAGE_LENGTH + " characters.");
        }
    }

    protected boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            return false;
        }

        String[] parts = name.split("\\s+");
        if (parts.length > 1) {
            return false;
        }

        return true;
    }

    private boolean isValidMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        if (message.length() < MIN_MESSAGE_LENGTH || message.length() > MAX_MESSAGE_LENGTH) {
            return false;
        }
        return true;
    }

    private enum MessageType {
        NAME_CHANGE,
        EXIT,
        HELP,
        NORMAL
    }
}
