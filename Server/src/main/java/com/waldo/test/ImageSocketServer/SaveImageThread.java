package com.waldo.test.ImageSocketServer;

import com.waldo.test.Main;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class SaveImageThread extends Thread {

    private final static Logger logger = Logger.getLogger(SaveImageThread.class);

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
            logger.error("Can not save image, image name is invalid..");
            return;
        }

        if (image == null) {
            logger.error("Can not save image, image name empty..");
            return;
        }

        String extension = FilenameUtils.getExtension(imageName);
        if (extension.isEmpty()) {
            extension = "JPG";
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
                ImageIO.write(image, extension, imageFile);
            } catch (IOException e) {
                logger.error("Can not save image" , e);
            }
        } else {
            logger.error("Can not save image, folder can not be found or created..");
        }
    }
}
