package de.fhbrandenburg.ristpr.server;

import de.fhbrandenburg.ristpr.util.ConnectToRDS;
import de.fhbrandenburg.ristpr.util.Loger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Psycho on 27.01.2016.
 */
public class Server implements Runnable {

    private InetAddress host;
    private InetAddress IP;
    private int serverPort;
    private ServerSocket serverSocket;
    private ArrayList<Connection> connections;
    private ConnectToRDS database;
    private ArrayList<String[]> chatMessages;
    private ArrayList<String[]> linkList;

    public Server(String IPAdress, int port) {
        try {
            host = InetAddress.getLocalHost();
            IP = InetAddress.getByName(IPAdress);
            serverPort = port;
            serverSocket = new ServerSocket(serverPort, 1000, IP);
            database = new ConnectToRDS();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Loger.ERR("Server konnte nicht gestartet werden.");
            e.printStackTrace();
        }

        connections = new ArrayList<Connection>();

    }

    /**
     * wartet auf neuen Clienten
     * legt eine neues Object Connetion(Client) in die Connetion-List welches sich selbst als Thread startet.
     * stutf den Clienten als identifiziert ein.
     */
    public void run() {
        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                database.execute("INSERT INTO logs (log) VALUES ('" + socket.getInetAddress() + " | " + socket.getInetAddress().getHostName() + "')");
                Connection connection = new Connection(this, database, socket, input, output);

                connection.sendMsgAndFlush(":" + getHost() + " NOTICE " + connection.getNick() + " :*** Connection accepted. Looking up your hostname...");
                connection.sendMsgAndFlush(":" + getHost() + " NOTICE " + connection.getNick() + " :*** Found your Hostname");
                connection.setNick("User" + connection.getThreadName().substring(connection.getThreadName().length() - 1));
                connection.setConState(ConnState.IDENTIFIED_AS_CLIENT);

                this.connections.add(connection);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * updated die Liste der Nachrichten
     * und ruft die sendMessage-methode jedes Clienten auf.
     */
    public void updateMessages() {  //Wird alle 2 sec aufgeruffen da der Thread im 2 sec schläft.
        chatMessages = database.getRecord("SELECT ID, message, userName, channel FROM messages LIMIT 100");
        linkList = database.getLinkList("SELECT ID, link FROM images LIMIT 100");
        Iterator it = connections.iterator();
        while (it.hasNext()) {
            Connection c = (Connection) it.next();
            c.sendMessages(chatMessages);
            c.sendLinks(linkList);
        }
    }

    /**
     * löscht alle Nutzer aus der DB die sich länger als 90 sec nicht mehr gemeldet haben (Ping)
     */
    public void updateServer() {    //Wird alle 2 sec aufgeruffen da der Thread im 2 sec schläft.
        database.execute("DELETE FROM users WHERE lastAction < '" + (System.currentTimeMillis() - 90000) + "'");
    }

    public ServerSocket getSocket() {
        return serverSocket;
    }

    public InetAddress getHost() {
        return host;
    }

    public ArrayList<String[]> getChatMessages() {
        return chatMessages;
    }
}
