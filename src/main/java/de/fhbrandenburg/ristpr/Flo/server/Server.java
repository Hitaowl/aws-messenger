package de.fhbrandenburg.ristpr.Flo.server;

import de.fhbrandenburg.ristpr.Flo.util.Loger;
import de.fhbrandenburg.ristpr.Flo.util.connectRDS;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Psycho on 25.01.2016.
 */
public class Server {

    private InetAddress Host;
    private InetAddress IP;
    private ServerSocket serverSocket;
    private ArrayList<PrintWriter> listClientWriter;
    private connectRDS database;
    private MessageWriter message;
    private ArrayList<String[]> chatMessages;


    public Server(String ipAdress, int port) {

        try {
            Host = InetAddress.getLocalHost();
            IP = InetAddress.getByName(ipAdress);
            serverSocket = new ServerSocket(port, 1000, IP);
            listClientWriter = new ArrayList<PrintWriter>();
            database = new connectRDS();

            Loger.LOG("Server wurde erfolgreich gestartet");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Loger.ERR("Server konnte nicht gestartet werden.");
            e.printStackTrace();
        }

    }

    public void listenToClients() {

        while (true) {

            try {
                Socket client = serverSocket.accept();

                PrintWriter writer = new PrintWriter(client.getOutputStream());
                listClientWriter.add(writer);

                ClientHandler clientThread = new ClientHandler(this, database, client);
                clientThread.start();

                Iterator it = chatMessages.iterator();
                int i = 0;

                while (it.hasNext()) {

                    writer.println(chatMessages.get(i)[1]);
                    writer.flush();
                    i++;
                    it.next();
                }
                it.remove();

                Loger.LOG("neuer Client: " + clientThread.getName());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void getMessagesFromServer() {
        chatMessages = database.getRecord("SELECT ID, message FROM messages LIMIT 100");
    }

    public ArrayList<String[]> getMessages() {
        return chatMessages;
    }

    public void startMesages() {
        message = new MessageWriter(this);
        message.start();
    }

    public void sendToAllClients(String message) {
        Iterator it = listClientWriter.iterator();

        while (it.hasNext()) {
            PrintWriter writer = (PrintWriter) it.next();
            writer.println(message);
            writer.flush();
        }
    }

    public void executeQuery(String sql) {
        database.execute(sql);
    }

}
