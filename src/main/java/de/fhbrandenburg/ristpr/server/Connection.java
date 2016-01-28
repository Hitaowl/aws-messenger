package de.fhbrandenburg.ristpr.server;

import de.fhbrandenburg.ristpr.util.ConnectToRDS;
import de.fhbrandenburg.ristpr.util.Loger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Psycho on 27.01.2016.
 */
public class Connection implements Runnable {

    private Thread thread;
    private Server server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ConnState connState;
    private String nick;
    private AbstractList<String> channel;
    private ArrayList<String[]> chatMessages;
    private InetAddress host;
    private String message;
    private ConnectToRDS database;
    private Boolean isInterupted;
    private long time;
    private int messageID;

    public Connection(Server server, ConnectToRDS database, Socket socket, BufferedReader input, PrintWriter output) {
        this.server = server;
        this.socket = socket;
        this.reader = input;
        this.writer = output;
        this.isInterupted = false;
        this.connState = ConnState.UNIDENTIFIED;
        this.nick = "*";
        this.channel = new ArrayList<String>();
        this.host = socket.getInetAddress();
        this.database = database;
        thread = new Thread(this);
        thread.start();
        this.messageID = 0;
        this.time = System.currentTimeMillis();
    }

    public void run() {
        while (!isInterupted) {
            if (getState() == ConnState.IDENTIFIED_AS_CLIENT) {
                message = "";
                try {
                    while ((message = reader.readLine()) != null) {
                        //Loger.LOG(""+(1+ message.indexOf(":",(message.indexOf(":")+2))));
                        //switch (message.substring((message.indexOf(":")+2),message.indexOf(":",(message.indexOf(":")+2))).replaceAll(" ","")) {
                        switch (message.split(" ")[0]) {
                            case "NICK":
                                if ((message.split(" ")[1].length()) <= 9) {
                                    String name;
                                    name = database.getUserName("SELECT userName FROM users WHERE userName = '" + message.split(" ")[1] + "'");
                                    if (!name.equals(message.split(" ")[1])) {
                                        setNick(message.split(" ")[1]);
                                        sendMsgAndFlush("NOTICE " + getNick() + "  *** Hello " + getNick());
                                        database.execute("INSERT INTO users (userName, serverName, lastAction, userHost) VALUES ('" + getNick() + "','" + server.getHost().getHostName() +
                                                "','" + System.currentTimeMillis() + "','" + getHostName() + "')");
                                        setChannel("#default");
                                        sendMsgAndFlush(":" + getNick() + "!*@" + getHostName() + " JOIN #default");
                                    }
                                    sendMsgAndFlush("NOTICE " + getNick() + " *** Nick already exist. Disconnect...");
                                } else {

                                    sendMsgAndFlush("NOTICE " + getNick() + " *** NICK length must be in-between 1 and 9 characters. Disconnect...");
                                }
                                Loger.LOG(getNick() + " hat sich verbunden");
                                setState(ConnState.CONNECTED_AS_CLIENT);

                                break;
                            case "PART":
                                if (channel.contains(message.split(" ")[1])) {
                                    channel.remove(channel.indexOf(message.split(" ")[1]));
                                }
                                break;
                            case "LINK":
                                break;

                            case "QUIT":
                                Loger.LOG(getNick() + " hat die Verbindung getrennt");
                                kill();
                                break;

                            case "PONG":
                                database.execute("UPDATE users SET lastAction = '" + System.currentTimeMillis() + "'  WHERE userName = '" + getNick() +
                                        "' AND serverName = '" + server.getHost().getHostName() + "' AND userHost = '" + getHostName() + "'");
                                break;

                            case "PRIVMSG":
                                database.execute("INSERT INTO messages (userID, userName, message, msgIndex, channel) VALUES ('0815','" +
                                        getNick() + "', '" + message.substring(message.indexOf(":") + 1) + "', '1','" + message.split(" ")[1] + "')");
                                break;
                            case "JOIN":
                                setChannel(message.split(" ")[1]);
                                sendMsgAndFlush(":" + getNick() + "!*@" + getHostName() + " JOIN " + message.split(" ")[1]);
                                setID(0);
                                break;
                        }

                    }
                } catch (IOException e) {
                    Loger.LOG(getNick() + " hat die Verbindung verloren");
                    kill();
                }
            }
        }

    }

    public void sendMsgAndFlush(String message) {
        writer.println(message);
        writer.flush();
    }

    public void kill() {
        connState = ConnState.DISCONNECTED;
        database.execute("DELETE FROM users WHERE userName = '" + getNick() + "' AND serverName = '" + server.getHost().getHostName() + "' AND userHost = '" + getHostName() + "'");
        try {
            thread.interrupt();
            socket.close();
            this.isInterupted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessages() {
        if (getState() == ConnState.CONNECTED_AS_CLIENT) {
            if ((this.time + 30000) < System.currentTimeMillis()) {
                this.time = System.currentTimeMillis();
                sendMsgAndFlush("PING " + getNick());
            }
            chatMessages = server.getChatMessages();
            Iterator it = chatMessages.iterator();
            int i = 0;
            int id = 0;

            while (it.hasNext()) {
                id = Integer.parseInt(chatMessages.get(i)[0]);
                if (id > getID()) {
                    setID(id);
                    if (getState() != ConnState.DISCONNECTED) {
                        if (channel.contains(chatMessages.get(i)[3])) {
                            sendMsgAndFlush(":" + chatMessages.get(i)[2] + "! PRIVMSG " + chatMessages.get(i)[3] + " :" + chatMessages.get(i)[1]);
                        }
                    }
                }
                i++;
                it.next();
            }
        }
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Boolean inChannel(String channel) {
        return this.channel.contains(channel);
    }

    public void setChannel(String channel) {
        this.channel.add(channel);
    }

    public String getHostName() {
        return host.getHostName();
    }

    public String getThreadName() {
        return thread.getName();
    }

    public void setID(int id) {
        messageID = id;
    }

    public int getID() {
        return messageID;
    }

    public void setState(ConnState state) {
        connState = state;
    }

    public ConnState getState() {
        return connState;
    }
}
