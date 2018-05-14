package com.waldo.test.ImageSocketServer;

import com.waldo.test.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

class TransmitThread extends ImageThread {

    TransmitThread(String imageName, ImageType imageType, OnThreadDoneListener threadDoneListener) throws IOException {
        super(imageName, imageType, threadDoneListener);
    }

    @Override
    void doInBackground(Socket clientSocket, String imageName, ImageType imageType) {

        try {

//            if (!imageName.endsWith(".jpg")) {
//                imageName += ".jpg";
//            }

            // Read image
            Path imagePath = Paths.get(Main.rootDirectory, imageType.getFolderName(), imageName);

            File file = new File(imagePath.toUri());
            BufferedImage image = ImageIO.read(file);

            // Send image
            if (image == null) {
                image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            }
            ImageIO.write(image, "JPG", clientSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}