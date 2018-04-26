package com.waldo.test.ImageSocketServer;

import com.waldo.test.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

class TransmitThread extends SocketThread {

    TransmitThread() throws Exception {
        super(0);
    }

    @Override
    void doInBackground() {
        try {
            Socket server = serverSocket.accept();

            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));

            BufferedImage image = null;
            String imageInfo = br.readLine();

            try {
                String[] split = imageInfo.split(";");
                String typeStr = split[0];
                String name = split[1];
                ImageType type = ImageType.fromInt(Integer.valueOf(typeStr));

                if (!name.endsWith(".jpg")) {
                    name += ".jpg";
                }

                Path imagePath = Paths.get(Main.rootDirectory, type.getFolderName(), name);

                File file = new File(imagePath.toUri());
                image = ImageIO.read(file);
            } catch (Exception e) {
                System.err.println("Failed to transmit image, " + e);
            }

            if (image == null) {
                image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
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