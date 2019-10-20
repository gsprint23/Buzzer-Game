/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Middleman between BuzzerServer (backend model) and BuzzerGUI (frontend view)
 *
 * @author Gina Sprint
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BuzzerController implements ActionListener {
    protected BuzzerGUI gui;
    protected BuzzerServer server;

    public BuzzerController(BuzzerServer server) {
        this.server = server;
        this.server.setController(this);

        // running an initial swing thread for setup
        // see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui = new BuzzerGUI(BuzzerController.this);
                server.startListening();
                System.out.println("Back from startListening(), listening for client connections...");
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (e.getActionCommand() == "Clear") {
            server.clearResponses();
            gui.responseListModel.clear();
            Toolkit.getDefaultToolkit().beep();
        }
        else if (e.getActionCommand() == "Score") {
            //String name = responseListModel.elementAt(responseList.getSelectedIndex());
            //name = name.substring(name.indexOf(':') + 1);
            server.scoreParticipant(gui.responseList.getSelectedIndex());
            gui.scoreListModel.clear();
            ArrayList<String> scores = server.getParticipantScores();
            for (String scoreStr: scores) {
                gui.scoreListModel.addElement(scoreStr);
            }
        }
        else if (e.getActionCommand() == "Stop") {
            stopListening();
            button.setText("Start Listening");
            button.setActionCommand("Start");
        }
        else if (e.getActionCommand() == "Start") {
            startListening();
            button.setText("Stop Listening");
            button.setActionCommand("Stop");
        }
        else if (e.getActionCommand() == "Quit") {
            stopListening();
            System.exit(0);
        }
    }

    public void startListening() {
        server.startListening();
    }

    public void stopListening() {
        server.stopListening();
    }


    public void responseReceived(String response) {
        int numResponses = gui.responseListModel.size();
        int position = numResponses + 1;
        gui.responseListModel.addElement(position + ":" + response);
        if (gui.responseListModel.size() == 1) {
            gui.responseList.setSelectedIndex(0);
        }
    }

    public void updateClientComponents() {
        List<Participant> clients = server.getParticipants();
        gui.clientCountLabel.setText("Active Participants: " + clients.size());
        gui.clientListModel.clear();
        for (Participant p : clients) {
            gui.clientListModel.addElement(p.getName());
        }
    }

    public void windowIsClosing() {
        server.stopListening();
    }
}
