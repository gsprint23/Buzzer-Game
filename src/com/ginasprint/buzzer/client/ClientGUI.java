/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * GUI for a participant/player in the Buzzer! Game
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame implements ActionListener {
    protected static final int FONT_SIZE = 18;

    private ClientController controller;
    protected JTextField nameTextField;

    public ClientGUI(ClientController controller) {
        super("Buzzer! Participant");

        this.controller = controller;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        this.pack();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controller.windowIsClosing();
            }
        });
        this.setVisible(true);
    }

    private void setupUI() {
        JPanel topPanel = new JPanel();

        JLabel nameLabel = new JLabel("Enter your Name: ");
        nameLabel.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        topPanel.add(nameLabel);
        nameTextField = new JTextField(15);
        nameTextField.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        topPanel.add(nameTextField);

        JButton buzzButton = new JButton("Buzz In!!");
        buzzButton.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        buzzButton.addActionListener(this);

        JPanel panel = (JPanel) getContentPane();
        panel.add(topPanel, BorderLayout.PAGE_START);
        panel.add(buzzButton, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = nameTextField.getText();
        controller.buzzIn(name);
    }
}
