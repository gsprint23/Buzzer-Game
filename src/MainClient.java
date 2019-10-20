/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * BuzzerClient connects to BuzzerServer
 * The user types their name, then presses enter to "buzz in"
 * If the BuzzerServer closes gracefully, it'll send a "closing" message to all of its clients
 * Before closing its client sockets
 * BuzzerClient listens for that "closing" message and terminates upon receiving it
 *
 * @author Gina Sprint
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainClient {
    public static void main(String[] args) {
        String hostName = "localhost"; // or insert IP address here
        int portNumber = 8080;

        if (args.length == 2) {
            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
        }
        else {
            System.out.println("Usage: <hostname> <port number>");
            System.out.println("Using default hostname: " + hostName);
            System.out.println("Using default port number: " + portNumber);
        }

        connect(hostName, portNumber);
    }

    public static void connect(String hostName, int portNumber) {
        try {
            Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                if (stdIn.ready()) { // had data in buffer from console (keyboard system.in)
                    String userInput = stdIn.readLine();
                    if (userInput != null) {
                        out.println(userInput);
                    }
                }

                if (in.ready()) { // has data in buffer from server
                    String serverResponse = in.readLine(); // BuzzerServer only sends "closing" shutdown signal
                    if (serverResponse.equals("closing")) {
                        System.out.println("Shutting down");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
