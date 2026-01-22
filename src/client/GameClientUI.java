package client;

import utils.Protocol;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * GameClientUI - GUI Client for Rock Paper Scissors Game
 * Day 6: Socket integration with GUI
 */
public class GameClientUI extends JFrame {
    // Server connection settings
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    // Network components
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // UI Components
    private JTextField nameField;
    private JButton joinBtn;
    private JLabel statusLabel;
    private JButton rockBtn, paperBtn, scissorsBtn;
    private JLabel resultLabel;
    private JLabel scoreLabel;

    // Game state
    private int wins = 0;
    private int losses = 0;
    private int draws = 0;
    private boolean isConnected = false;

    public GameClientUI() {
        initializeUI();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUI() {
        // Window settings
        setTitle("Rock Paper Scissors Game");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); // Center window

        // Add window listener to cleanup on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                disconnect();
            }
        });

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("🎮 ROCK PAPER SCISSORS 🎮");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Connection panel
        JPanel connectionPanel = createConnectionPanel();
        mainPanel.add(connectionPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Status label
        statusLabel = new JLabel("Enter your name and click Join");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(70, 130, 180));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Game buttons panel
        JPanel gamePanel = createGameButtonsPanel();
        mainPanel.add(gamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Result label
        resultLabel = new JLabel("Result: - ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(resultLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Score label
        scoreLabel = new JLabel(getScoreText());
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scoreLabel);

        // Add main panel to frame
        add(mainPanel);
    }

    /**
     * Create the connection panel (name field + join button)
     */
    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        nameField = new JTextField(15);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        joinBtn = new JButton("Join Game");
        joinBtn.setFont(new Font("Arial", Font.BOLD, 14));
        joinBtn.setBackground(new Color(76, 175, 80));
        joinBtn.setForeground(Color.WHITE);
        joinBtn.setFocusPainted(false);

        // Add action listener (will be implemented in Day 6)
        joinBtn.addActionListener(e -> handleJoinButton());

        // Allow pressing Enter in name field to join
        nameField.addActionListener(e -> handleJoinButton());

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(joinBtn);

        return panel;
    }

    /**
     * Create the game buttons panel (Rock, Paper, Scissors)
     */
    private JPanel createGameButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Rock button
        rockBtn = new JButton("🪨 ROCK");
        rockBtn.setFont(new Font("Arial", Font.BOLD, 14));
        rockBtn.setPreferredSize(new Dimension(120, 50));
        rockBtn.setEnabled(false); // Disabled until joined
        rockBtn.addActionListener(e -> handleChoice("ROCK"));

        // Paper button
        paperBtn = new JButton("📄 PAPER");
        paperBtn.setFont(new Font("Arial", Font.BOLD, 14));
        paperBtn.setPreferredSize(new Dimension(120, 50));
        paperBtn.setEnabled(false);
        paperBtn.addActionListener(e -> handleChoice("PAPER"));

        // Scissors button
        scissorsBtn = new JButton("✂️ SCISSORS");
        scissorsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        scissorsBtn.setPreferredSize(new Dimension(140, 50));
        scissorsBtn.setEnabled(false);
        scissorsBtn.addActionListener(e -> handleChoice("SCISSORS"));

        panel.add(rockBtn);
        panel.add(paperBtn);
        panel.add(scissorsBtn);

        return panel;
    }

    /**
     * Handle Join button click - Connect to server
     */
    private void handleJoinButton() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter your name!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Disable input while connecting
        statusLabel.setText("Connecting to server...");
        joinBtn.setEnabled(false);
        nameField.setEnabled(false);

        // Connect to server in a background thread to avoid freezing UI
        new Thread(() -> {
            try {
                // Connect to server
                socket = new Socket(SERVER_HOST, SERVER_PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                isConnected = true;

                System.out.println("Connected to server at " + SERVER_HOST + ":" + SERVER_PORT);

                // Send CONNECT message with player name
                sendMessage(Protocol.createMessage(Protocol.CONNECT, name));

                // Start thread to listen for server messages
                startMessageListener();

            } catch (IOException e) {
                isConnected = false;
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to server!\nMake sure the server is running on " + SERVER_HOST + ":" + SERVER_PORT,
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    statusLabel.setText("Connection failed. Try again.");
                    joinBtn.setEnabled(true);
                    nameField.setEnabled(true);
                });
                System.err.println("Connection error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Handle game choice button click - Send choice to server
     */
    private void handleChoice(String choice) {
        if (!isConnected) {
            JOptionPane.showMessageDialog(
                this,
                "Not connected to server!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        System.out.println("Choice made: " + choice);

        // Send choice to server
        sendMessage(Protocol.createMessage(Protocol.CHOICE, choice));

        // Update status and disable buttons
        updateStatus("You chose " + choice + ". Waiting for opponent...");
        disableGameButtons();
    }

    /**
     * Enable game buttons
     */
    private void enableGameButtons() {
        rockBtn.setEnabled(true);
        paperBtn.setEnabled(true);
        scissorsBtn.setEnabled(true);
    }

    /**
     * Disable game buttons
     */
    private void disableGameButtons() {
        rockBtn.setEnabled(false);
        paperBtn.setEnabled(false);
        scissorsBtn.setEnabled(false);
    }

    /**
     * Update the status label
     */
    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    /**
     * Update the result label
     */
    public void updateResult(String result) {
        SwingUtilities.invokeLater(() -> resultLabel.setText("Result: " + result));
    }

    /**
     * Update score after a game
     */
    public void updateScore(String result) {
        if (result.contains("WIN") || result.contains("You win")) {
            wins++;
        } else if (result.contains("LOSE") || result.contains("You lose")) {
            losses++;
        } else if (result.contains("DRAW") || result.contains("Draw")) {
            draws++;
        }

        SwingUtilities.invokeLater(() -> scoreLabel.setText(getScoreText()));
    }

    /**
     * Get formatted score text
     */
    private String getScoreText() {
        return String.format("Score - Wins: %d | Losses: %d | Draws: %d", wins, losses, draws);
    }

    /**
     * Send a message to the server
     */
    private void sendMessage(String message) {
        if (out != null && isConnected) {
            out.println(message);
            System.out.println("Sent to server: " + message);
        }
    }

    /**
     * Start a thread to listen for messages from the server
     */
    private void startMessageListener() {
        new Thread(() -> {
            try {
                String message;
                while (isConnected && (message = in.readLine()) != null) {
                    System.out.println("Received from server: " + message);
                    final String serverMessage = message;

                    // Update GUI on Event Dispatch Thread
                    SwingUtilities.invokeLater(() -> handleServerMessage(serverMessage));
                }
            } catch (IOException e) {
                if (isConnected) {
                    System.err.println("Error reading from server: " + e.getMessage());
                    SwingUtilities.invokeLater(() -> {
                        updateStatus("Connection lost!");
                        JOptionPane.showMessageDialog(
                            this,
                            "Lost connection to server!",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    });
                }
            } finally {
                disconnect();
            }
        }).start();
    }

    /**
     * Handle messages received from the server
     */
    private void handleServerMessage(String message) {
        String type = Protocol.parseType(message);
        String content = Protocol.parseContent(message);

        switch (type) {
            case Protocol.WELCOME:
                updateStatus(content);
                break;

            case Protocol.WAITING:
                updateStatus(content);
                disableGameButtons();
                break;

            case Protocol.OPPONENT_FOUND:
                updateStatus(content);
                updateResult("Game starting!");
                enableGameButtons();
                break;

            case Protocol.RESULT:
                handleGameResult(content);
                break;

            case Protocol.OPPONENT_LEFT:
                updateStatus(content);
                updateResult("Opponent disconnected");
                disableGameButtons();
                break;

            case Protocol.ERROR:
                updateStatus("Error: " + content);
                JOptionPane.showMessageDialog(
                    this,
                    content,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                break;

            default:
                System.out.println("Unknown message type: " + type);
        }
    }

    /**
     * Handle game result message from server
     * Format: "WIN:You chose ROCK, Opponent chose SCISSORS"
     */
    private void handleGameResult(String content) {
        // Parse the result
        String[] parts = content.split(":", 2);
        if (parts.length < 2) {
            updateResult(content);
            return;
        }

        String outcome = parts[0]; // WIN, LOSE, or DRAW
        String details = parts[1];  // Details about choices

        // Update result label
        updateResult(outcome + " - " + details);

        // Update score
        updateScore(outcome);

        // Re-enable buttons for next round
        Timer timer = new Timer(2000, e -> {
            enableGameButtons();
            updateStatus("Ready for next round!");
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Disconnect from the server
     */
    private void disconnect() {
        isConnected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                sendMessage(Protocol.createMessage(Protocol.DISCONNECT, ""));
                socket.close();
            }
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    /**
     * Main method to launch the GUI
     */
    public static void main(String[] args) {
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GameClientUI gui = new GameClientUI();
            gui.setVisible(true);
            System.out.println("=== Rock Paper Scissors GUI Client ===");
            System.out.println("Day 6: Socket integration complete");
            System.out.println("Client ready! Enter your name and click Join to connect to " + SERVER_HOST + ":" + SERVER_PORT);
        });
    }
}
