package de.fhbrandenburg.ristpr.Flo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Psycho on 25.01.2016.
 */
public class ClientHandler implements Runnable {

    Server server;
    Socket client;
    BufferedReader reader;

    public ClientHandler(Server server, Socket client) {

        this.server = server;
        try {
            this.client = client;
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                server.sendToAllClients(message);
            }
        } catch (IOException e) {

        }
    }
}