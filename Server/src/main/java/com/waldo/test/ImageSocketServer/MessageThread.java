package com.waldo.test.ImageSocketServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MessageThread extends Thread {

    private ServerSocket serverSocket;

    public MessageThread(int port) throws Exception {
        this.serverSocket = new ServerSocket(port);
    }

    public void run() {
        while (true) {
            try {
                Socket server = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                OutputStreamWriter out = new OutputStreamWriter(server.getOutputStream());

                // Get input
                String input = in.readLine();
                String output = "";

                // TODO convert input
                System.out.println("Got: " + input);
                output = input;

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

            } catch (SocketTimeoutException st) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } catch (Exception ex) {
                System.out.println("Error: " + ex);
            }
        }
    }
}
