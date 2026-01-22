package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GameClientCLI {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        System.out.println("=== Simple Echo Client ===");
        System.out.println("Connecting to server at " + SERVER_ADDRESS + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println("Connected to server!");

            // Setup input and output streams
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(),
                    true // auto-flush
            );

            // Scanner to read user input from console
            Scanner scanner = new Scanner(System.in);

            System.out.println("\nYou can now send messages to the server.");
            System.out.println("Type 'bye' to exit.\n");

            String userInput;
            while (true) {
                // Get user input
                System.out.print("You: ");
                userInput = scanner.nextLine();

                // Send message to server
                out.println(userInput);

                // Receive echo from server
                String response = in.readLine();
                System.out.println("Server: " + response);

                // Exit if user typed "bye"
                if (userInput.equalsIgnoreCase("bye")) {
                    System.out.println("Disconnecting from server...");
                    break;
                }
            }

            scanner.close();
            System.out.println("Connection closed.");

        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
