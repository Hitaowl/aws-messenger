package de.fhbrandenburg.ristpr.Flo.server;

import de.fhbrandenburg.ristpr.Flo.util.connectRDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Psycho on 25.01.2016.
 */
public class ClientHandler implements Runnable {

    Server server;
    Socket client;
    BufferedReader reader;
    Thread thread = null;
    connectRDS database;
    int ID;

    public ClientHandler(Server server, connectRDS database, Socket client) {

        this.server = server;
        this.database=database;
        this.ID = 0;
        try {
            this.client = client;
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String message;
        ArrayList<String[]> chatmessage;
        chatmessage = server.getMessages();
        Iterator it = chatmessage.iterator();
        int i = 0;
        int id = 0;

        while (it.hasNext()) {
            id = Integer.parseInt(chatmessage.get(i)[0]);
            if (id > ID) {
                ID = id;
                server.sendToAllClients(chatmessage.get(i)[1]);
            }
            i++;
            it.next();
        }
        try {
            while ((message = reader.readLine()) != null) {
                server.executeQuery("INSERT INTO messages (userID, message, msgIndex) VALUES ('" + thread.getName().substring(thread.getName().length()-1) + "', '"+message+"', '1')");
                //server.sendToAllClients(message);
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