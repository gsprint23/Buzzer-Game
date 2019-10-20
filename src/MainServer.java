/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Compare to hardware implementations such as
 * - https://www.buzzersystems.com/
 * - https://www.quizgamebuzzers.com
 *
 * Model: BuzzerServer
 * View: BuzzerGUI
 * Controller: BuzzerController
 *
 * Client: MainClient
 *
 * @author Gina Sprint
 */

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
        BuzzerServer server = new BuzzerServer(portNumber);
        BuzzerController controller = new BuzzerController(server);
    }
}
