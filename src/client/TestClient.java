package client;

import utils.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * TestClient - Console client to test the multi-client server
 * This is a simple client for Day 3 testing
 */
public class TestClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running = true;

    public static void main(String[] args) {
        TestClient client = new TestClient();
        client.start();
    }

    public void start() {
        try {
            // Connect to server
            System.out.println("=== Rock Paper Scissors Test Client ===");
            System.out.println("Connecting to server at " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to server!\n");

            // Start a thread to listen for server messages
            Thread listenerThread = new Thread(this::listenForMessages);
            listenerThread.start();

            // Main thread handles user input
            handleUserInput();

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    /**
     * Listen for messages from the server in a separate thread
     */
    private void listenForMessages() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                handleServerMessage(message);
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Lost connection to server");
            }
        }
    }

    /**
     * Handle messages received from the server
     */
    private void handleServerMessage(String message) {
        String type = Protocol.parseType(message);
        String content = Protocol.parseContent(message);

        System.out.println("\n[SERVER] " + message);

        switch (type) {
            case Protocol.WELCOME:
                System.out.println("Successfully joined! " + content);
                break;

            case Protocol.WAITING:
                System.out.println("Waiting for opponent...");
                break;

            case Protocol.OPPONENT_FOUND:
                System.out.println("Opponent found! " + content);
                System.out.println("Type your choice: ROCK, PAPER, or SCISSORS");
                break;

            case Protocol.RESULT:
                System.out.println("Game result: " + content);
                System.out.println("\nType your choice for next round: ROCK, PAPER, or SCISSORS");
                break;

            case Protocol.OPPONENT_LEFT:
                System.out.println("Your opponent left the game");
                break;

            case Protocol.ERROR:
                System.out.println("Error: " + content);
                break;
        }

        System.out.print("> ");
    }

    /**
     * Handle user input from console
     */
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);

        // First, ask for player name
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        // Send CONNECT message
        sendMessage(Protocol.createMessage(Protocol.CONNECT, name));

        System.out.println("\nCommands:");
        System.out.println("  ROCK / PAPER / SCISSORS - Make your choice");
        System.out.println("  quit - Exit the game\n");
        System.out.print("> ");

        // Main input loop
        while (running) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                running = false;
                sendMessage(Protocol.DISCONNECT);
                break;
            }

            // Check if it's a valid choice
            String upperInput = input.toUpperCase();
            if (upperInput.equals(Protocol.ROCK) ||
                upperInput.equals(Protocol.PAPER) ||
                upperInput.equals(Protocol.SCISSORS)) {

                sendMessage(Protocol.createMessage(Protocol.CHOICE, upperInput));
            } else if (!input.isEmpty()) {
                System.out.println("Invalid input. Use ROCK, PAPER, or SCISSORS");
                System.out.print("> ");
            }
        }

        // Cleanup
        try {
            if (socket != null) {
                socket.close();
            }
            System.out.println("\nDisconnected from server. Goodbye!");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }

        scanner.close();
    }

    /**
     * Send a message to the server
     */
    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
