package de.fhbrandenburg.ristpr;

import de.fhbrandenburg.ristpr.server.Server;
import de.fhbrandenburg.ristpr.util.Constants;
import de.fhbrandenburg.ristpr.util.Loger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by Psycho on 25.01.2016.
 */
public class AWSMessanger {

    public static void main(String args[]) {

        Server serverObject = null;
        Thread serverThread = null;

        int serverPort = Constants.PORT;
        String IPAdress = getPublicDomain();

        Loger.LOG("AWS-Messanger Server v-%s wird gestartet...", Constants.VERSION);

        if (args.length >= 1) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Loger.ERR("Ihre eingabe wurde nicht als gültiger Port erkannt");
                System.exit(-1);
            }
        }



        // Server wird gestartet ---------------------------------------------------------
        serverObject = new Server(IPAdress, serverPort);
        serverThread = new Thread(serverObject);
        serverThread.start();

        // Server wartet auf Clienten ----------------------------------------------------
        //chatServer.startMesages();
        //chatServer.listenToClients();

        while (!serverObject.getSocket().isClosed()){

            serverObject.updateMessages();              //prüft auf neue Nachrichten und sendet sie an die Clienten*
            serverObject.updateServer();                //entfernt Nutzer die die Verbindung verloren haben* aus der DB
                                                        // *Nutzer tragen sich eigentlich selbständig aus
                                                        // -> trit eigentlich nur bei absturz eines Servers ein
            try {
                serverThread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * ermittel die Public-Domain
     * @return gibt den Domainnamen zurück
     */
    private static String getPublicDomain() {
        URL url = null;
        try {
            url = new URL("http://169.254.169.254/latest/meta-data/public-hostname");
        URLConnection conn = url.openConnection();
        Scanner s = new Scanner(conn.getInputStream());
        if (s.hasNext()) {
            return s.next();
        }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}


