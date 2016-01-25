package de.fhbrandenburg.ristpr;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/*
    simpler Client, verbindet sich zum angegeben Server auf dem spezifischen Port,
    macht ein- und ausgehende Streams auf und wartet auf UserInput und Antworten vom Server
 */
public class ClientChatroom {

    BufferedReader reader;

    public static void main(String args[]){

        int serverPort = 4444;
        String hostName = "localhost";
        String username = "Max"; // ersetzen durch Login, Platzhalter

        try{
            // Verbindung zum Server
            InetAddress serverHost = InetAddress.getByName(hostName);
            System.out.println("Verbinden zum Server auf Port: " + serverPort);
            Socket client = new Socket(serverHost, serverPort);
            System.out.println("Verbunden mit dem Server " + client.getRemoteSocketAddress());

            // Reader und Writer aufbauen       //
            PrintWriter writer = new PrintWriter(client.getOutputStream());
            BufferedReader reader = new BufferedReader( new InputStreamReader(client.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            String fromServerMessage;
            String toServerMessage;

            // solange man keine Escape Sequenz benutzt läuft die Schleife
            while(true) {

                toServerMessage = stdIn.readLine();
                if (toServerMessage != null){
                    System.out.println(username + ": " + toServerMessage);
                    writer.println(toServerMessage);
                    writer.flush();
                }

                if ((fromServerMessage = reader.readLine()) != null){
                    System.out.println(fromServerMessage);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
