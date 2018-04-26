package com.waldo.test.ImageSocketServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveImageThread extends Thread {

    private final BufferedImage image;
    private String filePath;

    SaveImageThread(BufferedImage image, String filePath) {
        this.image = image;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        if (image != null && filePath != null) {
            File imageFile = new File(filePath);
            try {
                ImageIO.write(image, "jpg", imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
