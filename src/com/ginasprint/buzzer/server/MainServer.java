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
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.server;

public class MainServer {
    public static void main(String[] args) {
        int portNumber = 8080;
        if (args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
        }
        else {
            System.out.println("Usage: <hostname> <port number>");
            System.out.println("Using default port number: " + portNumber);
        }
        Server server = new Server(portNumber);
        ServerController controller = new ServerController(server);
    }
}
