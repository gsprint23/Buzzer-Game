/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Listens for client connections on a thread
 * Listens for messages from active connection with a client on another thread
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.server;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Server {
    public static final String CLOSING = "closing";
    protected static final int POINTS = 10;
    protected static int portNumber = 8080;
    protected static boolean listening = false;

    protected ServerController controller;

    protected ServerSocket serverSocket;
    protected List<Participant> participants = new ArrayList<>();
    protected List<InetAddress> responses = new ArrayList<>();
    protected ClientConnectionThread worker;

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public void setController(ServerController controller) {
        this.controller = controller;
    }

    public void startListening() {
        listening = true;

        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // using a standard Thread not SwingWorker thread because
        // this is purely backend work that doesn't update the UI
        // the server has a thread that listens for new connection requests from clients
        new Thread(() -> { // lambda expression for run() (anonymous class implementation of Runnable)
            Socket clientSocket;
            while(listening) {
                try {
                    clientSocket = serverSocket.accept();
                    InetAddress inetAddress = clientSocket.getInetAddress();
                    Participant p;
                    if ((p = findParticipant(inetAddress)) != null) {
                        p.setClientSocket(clientSocket);
                    } else {
                        p = new Participant(clientSocket);
                        participants.add(p);
                    }
                    controller.updateClientComponents();

                    if (worker == null) {
                        worker = new ClientConnectionThread(this, controller);
                        worker.start();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }

    public void stopListening() {
        listening = false;
        worker = null;
        try {
            for (Participant s : participants) {
                if (!s.getClientSocket().isClosed()) {
                    System.out.println("Closing " + s.getName());
                    PrintWriter out = new PrintWriter(s.getClientSocket().getOutputStream(), true);
                    out.println("closing");
                    s.getClientSocket().close();
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        participants.clear();
        updateParticipantList();
        controller.updateClientComponents();
    }

    public Participant findParticipant(InetAddress inetAddress) {
        for (Participant p : participants) {
            if (p.getClientSocket().getInetAddress().equals(inetAddress)) {
                return p;
            }
        }
        return null;
    }

    public void updateParticipantList() {
        List<Participant> connectedList = new ArrayList<>();
        for (Participant p : participants) {
            if (!p.isClosed()) {
                connectedList.add(p);
            }
        }
        participants = connectedList;
    }

    public ArrayList<String> getParticipantScores() {
        Collections.sort(participants);
        Collections.reverse(participants);
        ArrayList<String> scores = new ArrayList<>();

        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            scores.add((i + 1) + ") " + p.name + " (" + p.score + " pts)");
        }
        return scores;
    }

    public void scoreParticipant(int responseIndex) {
        InetAddress responseAddress = responses.get(responseIndex);
        for (Participant p : participants) {
            if (p.getClientSocket().getInetAddress().equals(responseAddress)) {
                p.setScore(p.getScore() + POINTS);
            }
        }
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void clearResponses() {
        responses.clear();
    }

    public void clearScores() {
        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            p.setScore(0);
        }
    }
}

// execute long running tasks on background threads
// each connection to a client runs on its own thread
// so it doesn't block the main GUI event thread (AKA event dispatch thread)
// see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
class ClientConnectionThread extends Thread {
    private Server buzzerServer;
    private BufferedReader in;
    private ServerController buzzerController;
    private int nextParticipantIndex = 0;

    public ClientConnectionThread(Server buzzerServer, ServerController controller) {
        this.buzzerController = controller;
        this.buzzerServer = buzzerServer;
    }

    public void run() {
        while (Server.listening) {
            if (buzzerServer.getParticipants().size() > 0) {
                Participant client = getNextParticipant();
                try {
                    in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
                    if (in.ready()) {
                        String inputLine;
                        if ((inputLine = in.readLine()) != null) {
                            System.out.println("Read from client: " + inputLine);
                            if (inputLine.length() > 0) { // ignore empty messages
                                takeInputLineAction(client, inputLine);
                            }
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("Socket Exception: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage());
                }
            }
        }
    }

    private Participant getNextParticipant() {
        Participant client = buzzerServer.getParticipants().get(nextParticipantIndex);
        nextParticipantIndex++;
        nextParticipantIndex %= buzzerServer.participants.size();
        return client;
    }

    private void takeInputLineAction(Participant client, String inputLine) {
        if (inputLine.equals(Server.CLOSING)) {
            // client sent the closing message, remove it from the active participants list
            closeClientConnection(client);
        }
        if (!inputLine.equals(client.getName())) {
            // new name for this client, update list
            client.setName(inputLine);
            SwingUtilities.invokeLater(()-> {
                buzzerController.updateClientComponents();
            });
        }
        if (!buzzerServer.responses.contains(client.getClientSocket().getInetAddress())) {
            // we haven't received a response from this ip address yet
            buzzerServer.responses.add(client.getClientSocket().getInetAddress());
            final String inputLineFinal = inputLine;
            SwingUtilities.invokeLater(() -> {
                buzzerController.responseReceived(inputLineFinal);
            });
        }
    }

    private void closeClientConnection(Participant client) {
        System.out.println("Read " + Server.CLOSING + " from client " + client.getName() + ", closing socket");

        try {
            client.getClientSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            buzzerServer.updateParticipantList();
            buzzerController.updateClientComponents();
        });
    }
}
