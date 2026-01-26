package utils;

/**
 * Protocol - Defines all message types for Server-Client communication
 * This ensures both server and client speak the same "language"
 */
public class Protocol {
    // Client -> Server messages
    public static final String CONNECT = "CONNECT";           // Client sends name to join
    public static final String CHOICE = "CHOICE";             // Client sends game choice (ROCK/PAPER/SCISSORS)
    public static final String DISCONNECT = "DISCONNECT";     // Client wants to leave

    // Server -> Client messages
    public static final String WELCOME = "WELCOME";           // Server confirms connection
    public static final String WAITING = "WAITING";           // Waiting for opponent
    public static final String OPPONENT_FOUND = "OPPONENT_FOUND";  // Game can start
    public static final String RESULT = "RESULT";             // Game result
    public static final String ERROR = "ERROR";               // Error message
    public static final String OPPONENT_LEFT = "OPPONENT_LEFT";    // Opponent disconnected

    // Game choices
    public static final String ROCK = "ROCK";
    public static final String PAPER = "PAPER";
    public static final String SCISSORS = "SCISSORS";

    // Game results
    public static final String WIN = "WIN";
    public static final String LOSE = "LOSE";
    public static final String DRAW = "DRAW";

    // Message delimiters
    public static final String DELIMITER = ":";

    /**
     * Helper method to create formatted messages
     * Example: createMessage("CHOICE", "ROCK") returns "CHOICE:ROCK"
     */
    public static String createMessage(String type, String content) {
        return type + DELIMITER + content;
    }

    /**
     * Helper method to parse message type
     * Example: parseType("CHOICE:ROCK") returns "CHOICE"
     */
    public static String parseType(String message) {
        if (message == null || !message.contains(DELIMITER)) {
            return message;
        }
        return message.split(DELIMITER)[0];
    }

    /**
     * Helper method to parse message content
     * Example: parseContent("CHOICE:ROCK") returns "ROCK"
     */
    public static String parseContent(String message) {
        if (message == null || !message.contains(DELIMITER)) {
            return "";
        }
        String[] parts = message.split(DELIMITER, 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
