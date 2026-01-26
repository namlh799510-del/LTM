package server;

import game.GameLogic;
import utils.Protocol;

import java.io.*;
import java.net.Socket;

/**
 * ClientHandler - Handles one client connection in a separate thread
 * Each client gets their own ClientHandler instance running in its own thread
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;
    private String choice;
    private ClientHandler opponent;
    private boolean isReady = false;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error setting up client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("New client connected from: " + socket.getInetAddress());

            // Handle client communication
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from " + playerName + ": " + message);
                handleMessage(message);
            }

        } catch (IOException e) {
            System.out.println("Client disconnected: " + playerName);
        } finally {
            cleanup();
        }
    }

    /**
     * Handle incoming messages from the client
     */
    private void handleMessage(String message) {
        String type = Protocol.parseType(message);
        String content = Protocol.parseContent(message);

        switch (type) {
            case Protocol.CONNECT:
                handleConnect(content);
                break;

            case Protocol.CHOICE:
                handleChoice(content);
                break;

            case Protocol.DISCONNECT:
                cleanup();
                break;

            default:
                sendMessage(Protocol.createMessage(Protocol.ERROR, "Unknown command"));
        }
    }

    /**
     * Handle CONNECT message - player sends their name
     */
    private void handleConnect(String name) {
        this.playerName = name;
        sendMessage(Protocol.createMessage(Protocol.WELCOME, "Welcome " + name + "!"));
        System.out.println("Player connected: " + name);

        // Add to waiting list and try to find an opponent
        GameServer.addToWaitingList(this);
    }

    /**
     * Handle CHOICE message - player makes their game choice
     */
    private void handleChoice(String choiceStr) {
        if (!GameLogic.isValidChoice(choiceStr)) {
            sendMessage(Protocol.createMessage(Protocol.ERROR, "Invalid choice. Use ROCK, PAPER, or SCISSORS"));
            return;
        }

        this.choice = choiceStr.toUpperCase();
        this.isReady = true;

        System.out.println(playerName + " chose: " + choice);
        sendMessage(Protocol.createMessage(Protocol.WAITING, "Waiting for opponent's choice..."));

        // Check if both players are ready
        checkGameReady();
    }

    /**
     * Check if both players have made their choices and calculate result
     */
    private synchronized void checkGameReady() {
        if (opponent == null) {
            sendMessage(Protocol.createMessage(Protocol.ERROR, "No opponent paired yet"));
            return;
        }

        if (this.isReady && opponent.isReady) {
            // Both players ready - calculate result
            playGame();
        }
    }

    /**
     * Play the game - both players have made their choices
     */
    private void playGame() {
        GameLogic.Choice myChoice = GameLogic.Choice.valueOf(this.choice);
        GameLogic.Choice opponentChoice = GameLogic.Choice.valueOf(opponent.choice);

        String result = GameLogic.determineWinner(myChoice, opponentChoice);

        // Send results to both players
        String myResult;
        String opponentResult;

        if (result.equals("DRAW")) {
            myResult = Protocol.DRAW;
            opponentResult = Protocol.DRAW;
        } else if (result.equals("PLAYER1_WINS")) {
            myResult = Protocol.WIN;
            opponentResult = Protocol.LOSE;
        } else {
            myResult = Protocol.LOSE;
            opponentResult = Protocol.WIN;
        }

        // Send detailed result
        sendMessage(Protocol.createMessage(Protocol.RESULT,
            myResult + ":You chose " + choice + ", Opponent chose " + opponent.choice));
        opponent.sendMessage(Protocol.createMessage(Protocol.RESULT,
            opponentResult + ":You chose " + opponent.choice + ", Opponent chose " + choice));

        // Reset for next round
        this.isReady = false;
        this.choice = null;
        opponent.isReady = false;
        opponent.choice = null;

        System.out.println("Game completed: " + playerName + " vs " + opponent.playerName);
    }

    /**
     * Send a message to this client
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            System.out.println("Sent to " + playerName + ": " + message);
        }
    }

    /**
     * Pair this client with an opponent
     */
    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
        if (opponent != null) {
            sendMessage(Protocol.createMessage(Protocol.OPPONENT_FOUND,
                "Matched with opponent: " + opponent.getPlayerName()));
        }
    }

    /**
     * Clean up when client disconnects
     */
    private void cleanup() {
        try {
            if (opponent != null) {
                opponent.sendMessage(Protocol.createMessage(Protocol.OPPONENT_LEFT,
                    "Your opponent has left"));
                opponent.setOpponent(null);
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            // Remove from server's waiting list
            GameServer.removeFromWaitingList(this);
            System.out.println("Cleaned up connection for: " + playerName);
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    // Getters
    public String getPlayerName() {
        return playerName != null ? playerName : "Unknown";
    }

    public boolean hasOpponent() {
        return opponent != null;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }
}
