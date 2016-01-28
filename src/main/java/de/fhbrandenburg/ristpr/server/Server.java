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

    public void run() {
        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                Connection connection = new Connection(this, database, socket, input, output);

                connection.sendMsgAndFlush(":" + getHost() + " NOTICE " + connection.getNick() + " :*** Connection accepted. Looking up your hostname...");
                String key = connection.getHostName() + " on " + connection.getThreadName();
                connection.sendMsgAndFlush(":" + getHost() + " NOTICE " + connection.getNick() + " :*** Found your Hostname");
                connection.setNick("User"+connection.getThreadName().substring(connection.getThreadName().length() - 1));
                connection.sendMsgAndFlush(":" + getHost() + " NOTICE " + connection.getNick() + " :*** Your System generated Username is:" + "User" + connection.getThreadName().substring(connection.getThreadName().length() - 1));
                connection.setState(ConnState.IDENTIFIED_AS_CLIENT);

                this.connections.add(connection);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void updateMessages() {
        chatMessages = database.getRecord("SELECT ID, message, userName, channel FROM messages LIMIT 100");
        Iterator it = connections.iterator();
        while (it.hasNext()) {
            Connection c = (Connection) it.next();
            c.sendMessages();
        }
    }

    public void updateServer(){
        database.execute("DELETE FROM users WHERE lastAction < '"+ (System.currentTimeMillis()-90000) +"'");
    }

    public ServerSocket getSocket() {
        return serverSocket;
    }

    public InetAddress getHost()
    {
        return host;
    }

    public ArrayList<String[]> getChatMessages(){return chatMessages;}
}
