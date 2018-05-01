package com.waldo.test.ImageSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket clientSocket;
    private CommunicationThread.MessageListener messageListener;

    public ClientThread(Socket clientSocket, CommunicationThread.MessageListener messageListener) {
        this.clientSocket = clientSocket;
        this.messageListener = messageListener;
    }


    @Override
    public void run() {
        if (clientSocket != null) {

            try(PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String inputLine, outputLine;

                while ((inputLine = in.readLine()) != null) {
                    if (messageListener != null) {
                        SocketMessage inputMessage = SocketMessage.convert(inputLine);
                        if (inputMessage.isValid()) {
                            SocketMessage outputMessage = messageListener.onNewMessage(inputMessage);
                            outputLine = outputMessage.toString();
                            out.println(outputLine);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
