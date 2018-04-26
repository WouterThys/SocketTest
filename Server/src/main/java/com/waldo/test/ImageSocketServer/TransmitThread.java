package com.waldo.test.ImageSocketServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

class TransmitThread extends SocketThread {

    private String rootDirectory;

    TransmitThread(String rootDirectory) throws Exception {
        super(0);
        this.rootDirectory = rootDirectory;
    }

    @Override
    void doInBackground() {
        try {
            Socket server = serverSocket.accept();

            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));

            String imageName = br.readLine();

            if (!imageName.endsWith(".jpg")) {
                imageName += ".jpg";
            }

            BufferedImage image = null;
            try {
                File file = new File(rootDirectory + imageName);
                image = ImageIO.read(file);
            } catch (Exception e) {
                //
            }

            if (image == null) {
                image = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
            }

            ImageIO.write(image, "JPG", server.getOutputStream());

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }
}