package com.waldo.test.ImageSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationThread extends SocketThread {

    public interface MessageListener  {
        SocketMessage onNewMessage(SocketMessage message);
    }

    private MessageListener messageListener;

    public CommunicationThread(int port, MessageListener messageListener) throws IOException {
        super(port);
        this.messageListener = messageListener;
    }

    @Override
    void doInBackground() throws IOException {

        try(Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
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
        }

    }
}
