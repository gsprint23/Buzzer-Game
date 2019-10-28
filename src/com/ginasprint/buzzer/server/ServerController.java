/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Middleman Server (backend model) and ServerGUI (frontend view)
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ServerController implements ActionListener {
    protected ServerGUI gui;
    protected Server server;

    public ServerController(Server server) {
        this.server = server;
        this.server.setController(this);

        // running an initial swing thread for setup
        // see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui = new ServerGUI(ServerController.this);
                server.startListening();
                System.out.println("Back from startListening(), listening for client connections...");
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (e.getActionCommand() == "Clear") {
            clearScores();
        }
        else if (e.getActionCommand() == "Score") {
            scoreParticipant();
        }
        else if (e.getActionCommand() == "Stop") {
            stopListening();
            button.setText("Start New Session");
            button.setActionCommand("Start");
        }
        else if (e.getActionCommand() == "Start") {
            clearScores();
            clearLeaderboard();
            startListening();
            button.setText("Stop Session");
            button.setActionCommand("Stop");
        }
        else if (e.getActionCommand() == "Quit") {
            stopListening();
            System.exit(0);
        }
    }

    public void responseReceived(String response) {
        int numResponses = gui.responseListModel.size();
        int position = numResponses + 1;
        gui.responseListModel.addElement(position + ") " + response);
        if (gui.responseListModel.size() == 1) {
            gui.responseList.setSelectedIndex(0);
        }
    }

    public void updateClientComponents() {
        List<Participant> clients = server.getParticipants();
        gui.clientCountLabel.setText("Active Participants: " + clients.size());
        gui.clientListModel.clear();
        for (Participant p : clients) {
            gui.clientListModel.addElement(p.toString());
        }
    }

    public void windowIsClosing() {
        server.stopListening();
    }

    private void clearScores() {
        server.clearResponses();
        gui.responseListModel.clear();
        //Toolkit.getDefaultToolkit().beep();
    }

    private void scoreParticipant() {
        //String name = responseListModel.elementAt(responseList.getSelectedIndex());
        //name = name.substring(name.indexOf(':') + 1);
        server.scoreParticipant(gui.responseList.getSelectedIndex());
        gui.scoreListModel.clear();
        ArrayList<String> scores = server.getParticipantScores();
        for (String scoreStr: scores) {
            gui.scoreListModel.addElement(scoreStr);
        }
    }

    private void clearLeaderboard() {
        gui.scoreListModel.clear();
        server.clearScores();
    }

    private void startListening() {
        server.startListening();
    }

    private void stopListening() {
        server.stopListening();
    }
}
