package de.fhbrandenburg.ristpr.Flo.server;

import de.fhbrandenburg.ristpr.Flo.util.connectRDS;

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
    connectRDS database;
    String userName;


    public ClientHandler(Server server, connectRDS database, Socket client) {

        this.server = server;
        this.database=database;
        userName="UnknownUser";
        try {
            this.client = client;
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        //server.sendToAllClients(userName + " hat sich mit dem Server verbunden.");
        String message;
        try {
            while ((message = reader.readLine()) != null) {

                if (!message.substring(0,message.indexOf(":")).replaceAll(" ","").equals("UnknownUser")){
                    userName = message.substring(0,message.indexOf(":"));
                }

                server.executeQuery("INSERT INTO messages (userID, message, msgIndex) VALUES ('" + thread.getName().substring(thread.getName().length()-1) + "', '"+message+"', '1')");


            }
        } catch (IOException e) {
            //server.sendToAllClients(userName + " --> hat den Server verlassen.");
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