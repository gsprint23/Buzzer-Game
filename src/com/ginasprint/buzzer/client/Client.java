/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Tries to connect to the Server
 * Starts a background thread to listen for the "closing" message from the
 * Server that signifies the server is terminating the connection
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static com.ginasprint.buzzer.server.Server.CLOSING;

public class Client {
    private ClientController controller;
    private String hostName;
    private int portNumber;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;


    public Client(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void setControllerAndOpenConnection(ClientController controller) {
        this.controller = controller;
        openConnection();
    }

    private void openConnection() {
        try {
            clientSocket = new Socket(hostName, portNumber);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));

            new Thread(() -> {
                String serverResponse;
                try {
                    while ((serverResponse = in.readLine()) != null) {
                        // com.sprint.buzzer.server.BuzzerServer only sends "closing" shutdown signal
                        if (serverResponse.equals(CLOSING)) {
                            System.out.println("Shutting down");
                            controller.connectionClosed();
                            if (clientSocket != null) {
                                clientSocket.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            controller.connectionClosed();
        }
    }

    public void buzzIn(String name) {
        if (out != null) {
            out.println(name);
        }
    }

    public void closeConnection() {
        if (out != null) {
            out.println(CLOSING);
        }
    }

    // non-OOP cmd line solution
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
                    String serverResponse = in.readLine(); // com.sprint.buzzer.server.BuzzerServer only sends "closing" shutdown signal
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
