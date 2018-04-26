package com.waldo.test.ImageSocketServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ReceiveThread extends SocketThread {

    private String rootDirectory;

    ReceiveThread(String rootDirectory) throws IOException {
        super(0);
        this.rootDirectory = rootDirectory;
    }

    @Override
    void doInBackground() {
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
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }
}
