package de.fhbrandenburg.ristpr.Flo;

import de.fhbrandenburg.ristpr.Flo.util.Loger;

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

    public Server(String ipAdress, int port) {

        try {
            Host = InetAddress.getLocalHost();
            IP = InetAddress.getByName(ipAdress);
            serverSocket = new ServerSocket(port, 1000, IP);
            listClientWriter = new ArrayList<PrintWriter>();
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

                ClientHandler clientThread = new ClientHandler(this, client);
                clientThread.start();

                Loger.LOG("neuer Client: " + clientThread.getName());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendToAllClients(String message) {
        Iterator it = listClientWriter.iterator();

        while (it.hasNext()) {
            PrintWriter writer = (PrintWriter) it.next();
            writer.println(message);
            writer.flush();
        }
    }
}
