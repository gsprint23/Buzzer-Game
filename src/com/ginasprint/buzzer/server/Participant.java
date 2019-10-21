/**
 * Buzzer! app for playing live Jeopardy like games
 *
 * Participant represents a participant (AKA player) in the Buzzer! game
 * Wraps the the client socket with a name (most recently buzzed in string)
 * and a score for the leaderboard
 *
 * @author Gina Sprint
 */

package com.ginasprint.buzzer.server;

import java.net.Socket;

public class Participant implements Comparable<Participant> {
    public String name;
    public Socket clientSocket;
    public int score;

    public Participant(Socket socket) {
        this.clientSocket = socket;
        this.name = socket.getInetAddress().getHostAddress();
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }

    @Override
    public int compareTo(Participant o) {
        // used to sort the Leaderboard panel of the GUI
        if (this.score != o.score) {
            return this.score - o.score;
        }
        return this.name.compareTo(o.name);
    }
}
