package com.waldo.test.ImageSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MessageThread extends SocketThread {

    public interface MessageListener  {
        SocketMessage onNewMessage(SocketMessage message);
    }

    private MessageListener messageListener;

    public MessageThread(int port, MessageListener messageListener) throws Exception {
        super(port);
        this.messageListener = messageListener;
    }

    @Override
    void doInBackground() {

        try {
            Socket server = serverSocket.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            OutputStreamWriter out = new OutputStreamWriter(server.getOutputStream());

            // Get input
            String input = in.readLine();
            String output = "";

            // Handle input
            if (messageListener != null) {
                SocketMessage message = SocketMessage.convert(input);
                if (!message.getCommand().equals(SocketCommand.Invalid)) {
                    SocketMessage result = messageListener.onNewMessage(message);
                    output = result.toString();
                }
            }

            // Response
            out.write(output, 0, output.length());
            out.flush();

            // Close
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
