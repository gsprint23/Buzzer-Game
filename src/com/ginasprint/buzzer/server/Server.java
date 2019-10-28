/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Listens for client connections and creates a SwingWorker background thread
 * for each accepted, active connection with a client
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
// see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/worker.html
// uses paramterized types... the first one is the return type of doInBackground()
// second one is for parameterized type of the List elements in process()
// you would use process() to provide intermediate results, perhaps to update a progress bar of some sort
// not used here, but would be called on the main UI thread
// see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/interim.html
class ClientConnectionThread extends Thread {
    private Server buzzerServer;
    private BufferedReader in;
    private ServerController buzzerController;
    private int nextParticipantIndex = 0;

    public ClientConnectionThread(Server buzzerServer, ServerController controller) {
        this.buzzerController = controller;
        this.buzzerServer = buzzerServer;
    }

    // doInBackground() is called when execute() is called on the SwingWorker object
    // doInBackground() runs on a background thread and cannot update the UI
    // returns a String that can be accessed from done() via get() (not used here)
    // see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/simple.html
    // doInBackground() would call publish() if had intermediate results that
    // process() should inform the user of on the main UI thread
    // see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/interim.html
    public void run() {
        while (Server.listening) {
            if (buzzerServer.participants.size() > 0) {
                Participant client = buzzerServer.participants.get(nextParticipantIndex);
                nextParticipantIndex++;
                nextParticipantIndex %= buzzerServer.participants.size();
                try {
                    in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
                    if (in.ready()) {
                        String inputLine;
                        if ((inputLine = in.readLine()) != null) {
                            System.out.println("Read from client: " + inputLine);
                            if (inputLine.length() > 0) { // ignore empty messages
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
                        } else {
                            System.out.println("Read null from client " + client.getName() + ", closing socket");
                            client.getClientSocket().close();
                            SwingUtilities.invokeLater(() -> {
                                buzzerServer.updateParticipantList();
                                buzzerController.updateClientComponents();
                            });
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("Socket Exception: " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
