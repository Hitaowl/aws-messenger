package de.fhbrandenburg.ristpr.Flo.server;

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
    Thread thread = null;

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
            stop();
        }
    }

    public synchronized void start(){
        if (thread == null){
            thread = new Thread(this);
            thread.start();
        }
    }

    public synchronized void stop(){
        thread.interrupt();
    }

    public synchronized String getName(){
        return thread.getName();
    }
}