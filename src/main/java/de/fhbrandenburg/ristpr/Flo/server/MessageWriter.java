package de.fhbrandenburg.ristpr.Flo.server;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Psycho on 26.01.2016.
 */
public class MessageWriter implements Runnable {
    Thread thread = null;
    Server server;
    int ID;

    public MessageWriter(Server server) {
        this.server = server;
        this.ID = 0;
        thread = new Thread(this);
    }

    public void run() {
        while (true) {
            try {
                server.getMessagesFromServer();
                selectMesages();
                thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void selectMesages(){
        ArrayList<String[]> message;
        message = server.getMessages();
        Iterator it = message.iterator();
        int i = 0;
        int id = 0;

        while (it.hasNext()) {
            id = Integer.parseInt(message.get(i)[0]);
            if (id > ID) {
                ID = id;
                server.sendToAllClients(message.get(i)[1]);
            }
            i++;
            it.next();
        }
    }

    public synchronized void start() {
        if (thread != null) {
            thread.start();
        }
    }

    public synchronized void stop() {
        thread.interrupt();
    }

}
