package de.fhbrandenburg.ristpr.Flo;

import de.fhbrandenburg.ristpr.Flo.server.Server;
import de.fhbrandenburg.ristpr.Flo.util.Constants;
import de.fhbrandenburg.ristpr.Flo.util.Loger;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by Psycho on 25.01.2016.
 */
public class AWSMessanger {

    public static void main(String args[]) {

        int serverPort =Constants.PORT;

        Loger.LOG("AWS-Messanger Server v-%s wird gestartet...", Constants.VERSION);

        if (args.length >= 1) {
            try {
                serverPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Loger.ERR("Ihre eingabe wurde nicht als gültiger Port erkannt");
            }
        }



        // Server wird gestartet ---------------------------------------------------------
        Server chatServer = new Server("localhost", serverPort);   // getPublicDomain()
        // Server wartet auf Clienten ----------------------------------------------------
        chatServer.startMesages();
        chatServer.listenToClients();
    }

    private static String getPublicDomain() throws IOException {
        URL url = new URL("http://169.254.169.254/latest/meta-data/public-hostname");
        URLConnection conn = url.openConnection();
        Scanner s = new Scanner(conn.getInputStream());
        if (s.hasNext()) {
            return s.next();
        }
        return null;
    }

}


