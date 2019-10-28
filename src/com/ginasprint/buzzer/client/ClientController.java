/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Middleman between com.sprint.buzzer.client.Client (backend model) and com.sprint.buzzer.client.ClientGUI (frontend view)
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.client;

import javax.swing.*;

public class ClientController {
    private Client client;
    private ClientGUI gui;

    public ClientController(Client client) {
        this.client = client;
        this.client.setControllerAndOpenConnection(this);

        // running an initial swing thread for setup
        // see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/initial.html
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui = new ClientGUI(ClientController.this);
            }
        });
    }

    public void buzzIn(String name) {
        client.buzzIn(name);
    }

    public void connectionClosed() {
        JOptionPane.showMessageDialog(gui, "The Server Connection Closed. Terminating Program.",
                "Connection Closed", JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }

    public void windowIsClosing() {
        client.closeConnection();
    }
}
