package de.fhbrandenburg.ristpr.server;

import de.fhbrandenburg.ristpr.util.ConnectToRDS;
import de.fhbrandenburg.ristpr.util.Loger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * Created by Psycho on 27.01.2016.
 */
public class Connection extends Thread implements Runnable {


    private Server server;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private ConnState connState;
    private String nick;
    private ArrayList<String> channel;
    private InetAddress host;
    private String message;
    private ConnectToRDS database;
    private long time;
    private Map<String, Integer> messageID;
    private int linkID;

    public Connection(Server server, ConnectToRDS database, Socket socket, BufferedReader input, PrintWriter output) throws RuntimeException {
        this.server = server;
        this.socket = socket;
        this.reader = input;
        this.database = database;
        this.writer = output;
        this.connState = ConnState.UNIDENTIFIED;
        this.nick = "*";
        this.channel = new ArrayList<String>();
        this.host = socket.getInetAddress();
        this.time = System.currentTimeMillis();
        messageID = Collections.synchronizedMap(new HashMap<String, Integer>());
        try {
            this.message = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.message.split(" ")[0].equals("NICK")) {
            nick(message);
        } else {
            kill();
        }

        this.start();
    }

    public void run() {
        while (this.isAlive()) {
            if (getConState() == ConnState.IDENTIFIED_AS_CLIENT) {
                message = "";
                try {
                    while ((message = reader.readLine()) != null) {
                        switch (message.split(" ")[0]) {
                            case "NICK":
                                nick(message);
                                break;
                            case "PART":
                                if (channel.contains(message.split(" ")[1])) {
                                    channel.remove(channel.indexOf(message.split(" ")[1]));
                                }
                                break;
                            case "LINK":
                                database.execute("INSERT INTO images (link) VALUES ('" + message.split(" ")[1] + "'");
                                break;

                            case "QUIT":
                                Loger.LOG(getNick() + " hat die Verbindung getrennt");
                                kill();
                                return;

                            case "PONG":
                                database.execute("UPDATE users SET lastAction = '" + System.currentTimeMillis() + "'  WHERE userName = '" + getNick() +
                                        "' AND serverName = '" + server.getHost().getHostName() + "' AND userHost = '" + getHostName() + "'");
                                break;

                            case "PRIVMSG":
                                database.execute("INSERT INTO messages (userID, userName, message, msgIndex, channel) VALUES ('0815','" +
                                        getNick() + "', '" + message.substring(message.indexOf(":") + 1) + "', '1','" + message.split(" ")[1] + "')");
                                break;
                            case "JOIN":
                                setID(message.split(" ")[1], 0);
                                setChannel(message.split(" ")[1]);
                                sendMsgAndFlush(":" + getNick() + "!*@" + getHostName() + " JOIN " + message.split(" ")[1]);
                                break;
                        }

                    }
                } catch (IOException e) {
                    if (this.isAlive()) {
                        Loger.LOG(getNick() + " hat die Verbindung verloren");
                    }
                    kill();
                    return;
                }
            }
        }

    }

    /**
     * schickt die übergebene Nachricht an den Client
     * der Connection
     *
     * @param message zu übergebende Nachricht
     */
    public void sendMsgAndFlush(String message) {
        writer.println(message);
        writer.flush();
    }

    /**
     * sollte eigentlich den Thread beenden "interrupten" geht aber irgendwie nicht richtig.
     * schließt das Socket und setzt den Thread als interrupted
     */
    public void kill() {
        connState = ConnState.DISCONNECTED;
        database.execute("DELETE FROM users WHERE userName = '" + getNick() + "' AND serverName = '" + server.getHost().getHostName() + "' AND userHost = '" + getHostName() + "'");
        try {
            this.interrupt();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * schickt alle 30 sec ein Ping an den Client
     * schickt alle neuen Nachrichten an den Client
     *
     * @param chatMessages Eine Liste der Messanges
     */
    public void sendMessages(ArrayList<String[]> chatMessages) {
        if (getConState() == ConnState.CONNECTED_AS_CLIENT) {

            if ((this.time + 30000) < System.currentTimeMillis()) {
                this.time = System.currentTimeMillis();
                sendMsgAndFlush("PING " + getNick());
            }

            Iterator it = chatMessages.iterator();
            int i = 0;
            int id = 0;

            while (it.hasNext()) {
                id = Integer.parseInt(chatMessages.get(i)[0]);  // ID schon versendet?
                if (channel.contains(chatMessages.get(i)[3]))   // ist der Client in dem entsprechendem channel?
                    if (id > getID(chatMessages.get(i)[3])) {
                        setID(chatMessages.get(i)[3], id);
                        if (getConState() != ConnState.DISCONNECTED) {
                            sendMsgAndFlush(":" + chatMessages.get(i)[2] + "! PRIVMSG " + chatMessages.get(i)[3] + " :" + chatMessages.get(i)[1]);
                        }
                    }
                i++;
                it.next();
            }
        }
    }


    public void sendLinks(ArrayList<String[]> linkList) {
        if (getConState() == ConnState.CONNECTED_AS_CLIENT) {
            Iterator it = linkList.iterator();
            int i = 0;
            int id = 0;

            while (it.hasNext()) {
                id = Integer.parseInt(linkList.get(i)[0]);
                if (id > getLinkID()) {
                    setLinkID(id);
                    if (getConState() != ConnState.DISCONNECTED) {
                        sendMsgAndFlush(":" + server.getHost() + " NOTICE " + getNick() + " :LINK " + linkList.get(i)[1]);
                    }
                }
                i++;
                it.next();
            }
        }
    }

    public void nick(String message){
        if ((message.split(" ")[1].length()) <= 9) {
            String name;
            name = database.getUserName("SELECT userName FROM users WHERE userName = '" + message.split(" ")[1] + "'");
            if (!name.equals(message.split(" ")[1])) {
                setNick(message.split(" ")[1]);
                sendMsgAndFlush(":" + server.getHost() + " NOTICE " + getNick() + " :*** Hello " + getNick());
                database.execute("INSERT INTO users (userName, serverName, lastAction, userHost) VALUES ('" + getNick() + "','" + server.getHost().getHostName() +
                        "','" + System.currentTimeMillis() + "','" + getHostName() + "')");
                setChannel("#default");
                this.messageID.put("#default", 0);
                setLinkID(0);
                sendMsgAndFlush(":" + getNick() + "!*@" + getHostName() + " JOIN #default");
            } else {
                sendMsgAndFlush("NOTICE " + getNick() + " *** Nick already exist. Disconnect...");
            }
        } else {

            sendMsgAndFlush("NOTICE " + getNick() + " *** NICK length must be in-between 1 and 9 characters. Disconnect...");
        }
        Loger.LOG(getNick() + " hat sich verbunden");
        setConState(ConnState.CONNECTED_AS_CLIENT);
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
        return this.getName();
    }

    public void setID(String channel, int id) {
        messageID.put(channel, id);
    }

    public int getID(String channel) {
        return messageID.get(channel);
    }

    public void setLinkID(int id) {
        linkID = id;
    }

    public int getLinkID() {
        return linkID;
    }

    public void setConState(ConnState state) {
        connState = state;
    }

    public ConnState getConState() {
        return connState;
    }
}
