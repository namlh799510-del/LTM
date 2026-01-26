package server;

import utils.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * GameServer - Multi-Client Rock-Paper-Scissors Server
 * Accepts multiple clients and pairs them for games
 */
public class GameServer {
    private static final int PORT = 12345;
    private static final List<ClientHandler> waitingClients = new ArrayList<>();
    private static final List<ClientHandler> allClients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== Rock Paper Scissors Game Server ===");
        System.out.println("Starting server on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);
            System.out.println("Waiting for clients to connect...\n");

            // Accept clients in an infinite loop
            while (true) {
                try {
                    // Accept a new client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New connection from: " + clientSocket.getInetAddress());

                    // Create a handler for this client
                    ClientHandler clientHandler = new ClientHandler(clientSocket);

                    // Add to all clients list
                    synchronized (allClients) {
                        allClients.add(clientHandler);
                    }

                    // Start a new thread for this client
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();

                    System.out.println("Total clients connected: " + allClients.size());

                } catch (IOException e) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Add a client to the waiting list and try to pair them with an opponent
     * This is called by ClientHandler after the client sends their name
     */
    public static synchronized void addToWaitingList(ClientHandler client) {
        System.out.println("Adding " + client.getPlayerName() + " to waiting list");

        // Check if there's someone waiting
        if (!waitingClients.isEmpty()) {
            // Pair with the first waiting client
            ClientHandler opponent = waitingClients.remove(0);

            // Pair them together
            client.setOpponent(opponent);
            opponent.setOpponent(client);

            System.out.println("Paired: " + client.getPlayerName() + " vs " + opponent.getPlayerName());

        } else {
            // No one waiting, add this client to waiting list
            waitingClients.add(client);
            client.sendMessage(Protocol.createMessage(Protocol.WAITING,
                "Waiting for an opponent to join..."));
            System.out.println(client.getPlayerName() + " is waiting for an opponent");
        }
    }

    /**
     * Remove a client from the waiting list
     */
    public static synchronized void removeFromWaitingList(ClientHandler client) {
        waitingClients.remove(client);
        allClients.remove(client);
        System.out.println("Removed " + client.getPlayerName() + " from server");
        System.out.println("Active clients: " + allClients.size());
    }

    /**
     * Get the number of clients waiting for an opponent
     */
    public static synchronized int getWaitingCount() {
        return waitingClients.size();
    }

    /**
     * Get total number of connected clients
     */
    public static synchronized int getTotalClients() {
        return allClients.size();
    }
}
