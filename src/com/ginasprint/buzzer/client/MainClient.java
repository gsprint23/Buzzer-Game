/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Compare to hardware implementations such as
 * - https://www.buzzersystems.com/
 * - https://www.quizgamebuzzers.com
 *
 * Server MVC
 * Model: com.sprint.buzzer.server.Server
 * View: com.sprint.buzzer.server.ServerGUI
 * Controller: com.sprint.buzzer.server.ServerController
 *
 * Client MVC
 * Model: com.sprint.buzzer.client.Client
 * View: com.sprint.buzzer.client.ClientGUI
 * Controller: com.sprint.buzzer.client.ClientController
 *
 * Two client modes:
 * 1. GUI mode: the user types their name in a JTextField, then presses a JButton to "buzz in"
 * 2. Cmd Line Mode: (each time) the user types their name, then presses keyboard enter to "buzz in"
 *
 * Note: If the Server closes gracefully, it'll send a "closing" message to all of its clients
 * Before closing its client sockets
 * Client listens for that "closing" message and terminates upon receiving it
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.client;

public class MainClient {
    static boolean GUI = true;

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

        if (GUI) {
            Client client = new Client(hostName, portNumber);
            ClientController controller = new ClientController(client);

        } else {
            // non-OOP command-line client solution
            Client.connect(hostName, portNumber);
        }
    }
}
