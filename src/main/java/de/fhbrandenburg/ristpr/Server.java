package de.fhbrandenburg.ristpr;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Max on 22.01.16.
 */
public class Server {
    public static void main (String args[]){
        int serverPort = 4444;
        boolean listening = true;
        ServerSocket serverSocket = null;

        try{
            serverSocket = new ServerSocket(serverPort);

            // Multithreading
            while(listening){
                new ServerThread(serverSocket.accept()).start();
            }
        }
        catch (IOException e){
            System.err.println(e);
            System.exit(-1);
        }
    }
}
