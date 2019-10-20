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
 * Client: BuzzerClient
 *
 * @author Gina Sprint
 */

public class MainServer {
    public static void main(String[] args) {
        BuzzerServer server = new BuzzerServer();
        BuzzerController controller = new BuzzerController(server);
    }
}
