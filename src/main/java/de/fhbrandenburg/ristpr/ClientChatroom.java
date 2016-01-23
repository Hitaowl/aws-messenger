package de.fhbrandenburg.ristpr;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/*
    simpler Client, verbindet sich zum angegeben Server auf dem spezifischen Port,
    macht ein- und ausgehende Streams auf und wartet auf UserInput und Antworten vom Server
 */
public class Client {

    public static void main(String args[]){

        int serverPort = 4444;
        String hostName = "localhost";
        String username = "Max"; // ersetzen durch Login, Platzhalter

        try{
            // Verbindung zum Server
            InetAddress serverHost = InetAddress.getByName(hostName);
            System.out.println("Verbinden zum Server auf Port: " + serverPort);
            Socket socket = new Socket(serverHost, serverPort);
            System.out.println("Verbunden mit dem Server " + socket.getRemoteSocketAddress());

            // Reader und Writer aufbauen
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromServer = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            String fromServerString;
            String toServerString;

            // solange man keine Escape Sequenz benutzt läuft die Schleife
            while((fromServerString = fromServer.readLine()) != null) {
                System.out.println("Server: " + fromServerString);

                toServerString = stdIn.readLine();
                if (toServerString != null){
                    System.out.println(username + ": " + toServerString);
                    toServer.println(toServerString);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
