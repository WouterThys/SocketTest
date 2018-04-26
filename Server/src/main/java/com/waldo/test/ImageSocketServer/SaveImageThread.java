package com.waldo.test.ImageSocketServer;

import com.waldo.test.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class SaveImageThread extends Thread {

    private final BufferedImage image;
    private String imageName;
    private ImageType imageType;

    SaveImageThread(BufferedImage image, String imageName, ImageType imageType) {
        this.image = image;
        this.imageName = imageName;
        this.imageType = imageType;
    }

    @Override
    public void run() {
        if (imageName == null || imageName.isEmpty()) {
            System.err.println("Can not save image, image name is invalid..");
            return;
        }

        if (image == null) {
            System.err.println("Can not save image, image name empty..");
            return;
        }

        if (!imageName.endsWith(".jpg")) {
            imageName += ".jpg";
        }

        Path folderPath = Paths.get(Main.rootDirectory, imageType.getFolderName());
        File imageFolder = new File(folderPath.toUri());

        boolean ok = true;
        if (!imageFolder.exists()) {
            ok = imageFolder.mkdirs();
        }

        if (ok) {
            Path path = Paths.get(imageFolder.toString(), imageName);
            File imageFile = new File(path.toUri());

            try {
                ImageIO.write(image, "jpg", imageFile);
            } catch (IOException e) {
                System.err.println("Can not save image, " + e);
            }
        } else {
            System.err.println("Can not save image, folder can not be found or created..");
        }
    }
}
