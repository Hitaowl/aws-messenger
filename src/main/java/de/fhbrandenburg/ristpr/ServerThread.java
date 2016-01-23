package de.fhbrandenburg.ristpr;

import java.io.*;
import java.net.Socket;

/**
 * Created by Max on 22.01.16.
 */
public class ServerThread extends Thread {

    private Socket socket = null;

    public ServerThread(Socket socket){

        this.socket = socket;
    }

    public void run(){

        try{
            System.out.println("Verbunden mit: " + socket.getRemoteSocketAddress());
            PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader fromClient = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String fromClientString;
            String toClientString;

            // initiale antwort zum Client
            toClient.println("Hello new User:" + socket.getRemoteSocketAddress());

            //das Messageprotokol muss hier rein

            // Endlosschleife zum hören und antworten bis escapesequenz kommt
            while((fromClientString = fromClient.readLine()) != null){
                toClientString = fromClientString + " SERVER";
                toClient.println(toClientString);
            }
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
