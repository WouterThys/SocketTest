package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.ImageType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SendFullContentTask {

    // 1. Read content of a folder
    // 2. Convert content of the folder to jpg images
    // 3. Send converted images to server

    public static final int STATE_READ = 1;
    public static final int STATE_CONVERT = 2;
    public static final int STATE_SEND = 3;

    public interface SendFullContentListener {
        void onError(Exception exception);
        void onUpdateState(int state, String description, String... args);
    }

    private Client client;
    private SendFullContentListener listener;
    private File imageFolder;
    private ImageType imageType;

    // Intermediate data
    private HashMap<String, BufferedImage> folderContent;

    public SendFullContentTask(Client client, SendFullContentListener listener, File imageFolder, ImageType imageType) {
        this.client = client;
        this.listener = listener;
        this.imageFolder = imageFolder;
        this.imageType = imageType;
    }

    public void startReading() {
        GetAllImagesTask getAllImagesTask = new GetAllImagesTask();
        getAllImagesTask.execute();
    }

    private void failedToSendContent(Exception exception) {
        if (listener != null) {
            listener.onError(exception);
        }
    }

    private void updateState(int state, String description, String... args) {
        if (listener != null) {
            listener.onUpdateState(state, description, args);
        }
    }

    private class GetAllImagesTask extends SwingWorker<Boolean, Void> {
        @Override
        protected Boolean doInBackground() throws Exception {
            if (client == null) {
                failedToSendContent(new Exception("Client is not connected.."));
                return false;
            }

            if (imageFolder == null || !imageFolder.exists() || !imageFolder.isDirectory()) {
                failedToSendContent(new Exception("Invalid folder.."));
                return false;
            }

            if (imageType == null) {
                failedToSendContent(new Exception("Invalid image type.."));
                return false;
            }

            //
            // 1. Read content of a folder
            //
            File[] listOfImages = imageFolder.listFiles();

            if (listOfImages == null || listOfImages.length == 0) {
                failedToSendContent(new Exception("Empty image folder.."));
                return false;
            }

            int numberOfFiles = listOfImages.length;
            updateState(STATE_READ, "Reading " + numberOfFiles + " images", String.valueOf(numberOfFiles));

            int progress = 0;
            folderContent = new HashMap<>();
            for (File file : listOfImages) {
                BufferedImage inputImage = ImageIO.read(file);
                if (inputImage != null) {
                    String name = file.getName();
                    name = name.split("\\.", 2)[0]; // Remove everything after '.'
                    folderContent.put(name, inputImage);
                }

                updateState(STATE_READ, "Caching images", String.valueOf(progress));
                progress++;
            }

            //
            // 2. Convert content of the folder to scaled jpg images
            //
            numberOfFiles = folderContent.size();
            updateState(STATE_CONVERT, "Converting " + numberOfFiles + " images", String.valueOf(numberOfFiles));

            progress = 0;
            for (String name : folderContent.keySet()) {
                BufferedImage oldImage = folderContent.get(name);
                folderContent.put(name, ImageUtils.convertImage(oldImage, imageType));

                updateState(STATE_READ, "Converting image " + name, String.valueOf(progress));
                progress++;
            }

            //
            // 3. Send converted images to server
            //
            numberOfFiles = folderContent.size();
            updateState(STATE_SEND, "Sending " + numberOfFiles + " images", String.valueOf(numberOfFiles));

            progress = 0;
            for (String name : folderContent.keySet()) {
                BufferedImage image = folderContent.get(name);

                try {
                    client.sendImage(image, name, imageType);
                    updateState(STATE_SEND, "Sending image " + name, String.valueOf(progress));
                } catch (Exception ex) {
                    updateState(STATE_SEND, "Sending image " + name + " failed", String.valueOf(progress));
                }

                progress++;
            }

            updateState(STATE_SEND, "Done");

            return true;
        }
    }
}
