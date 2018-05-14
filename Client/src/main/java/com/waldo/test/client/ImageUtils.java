package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.ImageType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageUtils {

    public static BufferedImage convertImage(File file, ImageType imageType) throws Exception {
        BufferedImage bufferedImage = null;
        if (file != null && file.exists()) {
            BufferedImage inputImage = ImageIO.read(file);
            bufferedImage = convertImage(inputImage, imageType);
        }
        return bufferedImage;
    }

    public static BufferedImage convertImage(BufferedImage inputImage, ImageType imageType) {
        return inputImage;
//        BufferedImage bufferedImage = null;
//            if (inputImage != null) {
//                Dimension dimension = getScaledDimension(inputImage.getWidth(), inputImage.getHeight(), imageType.getDimension());
//
//                Image tmp = inputImage.getScaledInstance(dimension.width, dimension.height, Image.SCALE_SMOOTH);
//                BufferedImage scaled = new BufferedImage(dimension.width, dimension.height, inputImage.getType());
//
//                Graphics2D g2d = scaled.createGraphics();
//                g2d.drawImage(tmp, 0, 0, null);
//                g2d.setComposite(AlphaComposite.Src);
//                g2d.dispose();
//
//                bufferedImage = new BufferedImage(scaled.getWidth(), scaled.getHeight(), inputImage.getType());
//                bufferedImage.createGraphics().drawImage(scaled, 0, 0, Color.WHITE, null);
//
//            }
//
//        return bufferedImage;
    }

    public static  Dimension getScaledDimension(int originalWidth, int originalHeight, Dimension boundary) {

        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = originalWidth;
        int new_height = originalHeight;

        // first check if we need to scale width
        if (originalWidth > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * originalHeight) / originalWidth;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * originalWidth) / originalHeight;
        }

        return new Dimension(new_width, new_height);
    }

}
