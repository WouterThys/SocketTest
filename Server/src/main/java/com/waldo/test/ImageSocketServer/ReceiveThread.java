package com.waldo.test.ImageSocketServer;

import com.waldo.test.Main;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

class ReceiveThread extends ImageThread {

    private final static Logger logger = Logger.getLogger(ReceiveThread.class);

    ReceiveThread(String imageName, ImageType imageType, OnThreadDoneListener onThreadDoneListener) throws IOException {
        super(imageName, imageType, onThreadDoneListener);
    }

    @Override
    void doInBackground(Socket clientSocket, String imageName, ImageType imageType) {

        try {
            // Read image
            BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(clientSocket.getInputStream()));

            // Save image
            logger.debug("Received image " + imageName + ", start save");
            SaveImageThread saveImageThread = new SaveImageThread(img, imageName, imageType);
            saveImageThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
