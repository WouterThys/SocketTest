package com.waldo.test.ImageSocketServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ReceiveThread extends Thread {

    private ServerSocket serverSocket;
    private String rootDirectory;

    public ReceiveThread(int port, String rootDirectory) throws Exception {
        this.serverSocket = new ServerSocket(port);
        this.rootDirectory = rootDirectory;
    }

    public void run() {
        while (true) {
            try {
                Socket server = serverSocket.accept();

                BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));

                String imageName = br.readLine();
                BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));

                if (!imageName.endsWith(".jpg")) {
                    imageName += ".jpg";
                }

                SaveImageThread saveImageThread = new SaveImageThread(img, rootDirectory + imageName);
                saveImageThread.start();

                try {
                    br.close();
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
