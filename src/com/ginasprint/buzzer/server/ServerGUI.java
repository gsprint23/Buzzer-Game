/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * GUI for the Buzzer! game
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.server;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerGUI extends JFrame {
    protected static final int FONT_SIZE = 18;
    protected static String ipAddress = "localhost";

    protected ServerController controller;

    // GUI component fields
    protected JLabel clientCountLabel;
    protected JList clientList;
    protected DefaultListModel<String> clientListModel;
    protected JList responseList;
    protected DefaultListModel<String> responseListModel;
    protected JList scoreList;
    protected DefaultListModel<String> scoreListModel;

    public ServerGUI(ServerController controller) {
        super("The Buzzer! Game");
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
        this.setVisible(true); // takes a little bit of time to setup UI
        // wait to set visible til pack the layout
    }

    private void setupUI() {
        JPanel listsPanel = new JPanel(new GridLayout(0,3));
        listsPanel.add(createClientListPanel());
        listsPanel.add(createResponseListPanel());
        listsPanel.add(createScoreListPanel());

        JPanel configPanel = createConfigPanel();
        JPanel buttonPanel = createButtonPanel();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(configPanel, BorderLayout.PAGE_START);
        panel.add(listsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.PAGE_END);

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private JPanel createClientListPanel() {
        clientListModel = new DefaultListModel<>();
        clientList = new JList(clientListModel);
        clientList.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane paneScrollPaneClient = new JScrollPane(clientList);
        paneScrollPaneClient.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPaneClient.setPreferredSize(new Dimension(250, 400));
        paneScrollPaneClient.setMinimumSize(new Dimension(250, 400));

        JPanel panel = new JPanel(new GridLayout(1,0));
        panel.add(paneScrollPaneClient);
        panel.setBorder(BorderFactory.createTitledBorder("Active Participants"));
        TitledBorder border = (TitledBorder) panel.getBorder();
        border.setTitleFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        return panel;
    }

    private JPanel createResponseListPanel() {
        responseListModel = new DefaultListModel<>();
        responseList = new JList(responseListModel);
        responseList.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        responseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        responseList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane paneScrollPane = new JScrollPane(responseList);
        paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        paneScrollPane.setPreferredSize(new Dimension(250, 400));
        paneScrollPane.setMinimumSize(new Dimension(250, 400));

        JPanel panel = new JPanel(new GridLayout(1,0));
        panel.add(paneScrollPane);
        panel.setBorder(BorderFactory.createTitledBorder("Buzzed In"));
        TitledBorder border = (TitledBorder) panel.getBorder();
        border.setTitleFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        return panel;
    }

    private JPanel createScoreListPanel() {
        scoreListModel = new DefaultListModel<>();
        scoreList = new JList(scoreListModel);
        scoreList.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        scoreList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scoreList.setLayoutOrientation(JList.VERTICAL);

        JScrollPane scoreScrollPane = new JScrollPane(scoreList);
        scoreScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scoreScrollPane.setPreferredSize(new Dimension(250, 400));
        scoreScrollPane.setMinimumSize(new Dimension(250, 400));

        JPanel panel = new JPanel(new GridLayout(1,0));
        panel.add(scoreScrollPane);
        panel.setBorder(BorderFactory.createTitledBorder("Leaderboard Rankings"));
        TitledBorder border = (TitledBorder) panel.getBorder();
        border.setTitleFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        return panel;
    }

    // returns 127.0.0.1, but we want public IP
    // private String getPublicIPAddress() {
    //     String ipAddressString = "";
    //     //String myHostName = "??";
    //     try {
    //         ipAddress = InetAddress.getLocalHost().getHostAddress();
    //         //String[] pieces = inetAddress.toString().split("/");
    //         //ipAddress = pieces[1];
    //         //myHostName = inetAddress.getHostName();
    //     } catch (UnknownHostException e) {
    //         e.printStackTrace();
    //     }
    //     return ipAddressString;
    // }

    private String getPublicIPAddress() {
        // from https://www.geeksforgeeks.org/java-program-find-ip-address-computer/
        // Find public IP address
        String ipAddressString = "";
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("google.com", 80));
            ipAddressString = socket.getLocalAddress().getHostAddress();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ipAddressString;
    }

    private JPanel createConfigPanel() {
        JLabel ipLabel = new JLabel();
        ipLabel.setFont(new Font("Default", Font.BOLD, FONT_SIZE));
        InetAddress inetAddress;
        ipAddress = getPublicIPAddress();
        ipLabel.setText("Server IP Address: " + ipAddress);// + " Hostname: " + myHostName);

        JPanel panel = new JPanel();
        panel.add(ipLabel);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 4));

        clientCountLabel = new JLabel();
        clientCountLabel.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        clientCountLabel.setText("Active Participants: 0");
        buttonPanel.add(clientCountLabel);

        JButton startStopButton = new JButton();
        startStopButton.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        startStopButton.setText("Stop Session");
        startStopButton.setActionCommand("Stop");
        startStopButton.addActionListener(this.controller);
        buttonPanel.add(startStopButton);

        JButton scoreButton = new JButton();
        scoreButton.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        scoreButton.setText("Score Participant");
        scoreButton.setActionCommand("Score");
        scoreButton.addActionListener(this.controller);
        buttonPanel.add(scoreButton);

        JButton clearButton = new JButton();
        clearButton.setFont(new Font("Default", Font.PLAIN, FONT_SIZE));
        clearButton.setText("Clear Responses");
        clearButton.setActionCommand("Clear");
        clearButton.addActionListener(this.controller);
        buttonPanel.add(clearButton);

        return buttonPanel;
    }

}
