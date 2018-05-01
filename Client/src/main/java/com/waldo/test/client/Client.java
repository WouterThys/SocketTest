package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.ImageSocketServer.SocketCommand;
import com.waldo.test.ImageSocketServer.SocketMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public interface ImageClientListener {
        void onConnected(String clientName);

        void onDisconnected(String clientName);

        void onImageTransmitted(String imageName, ImageType imageType);

        void onImageReceived(BufferedImage image, String imageName, ImageType imageType);
    }

    private static final int msPort = 31213;
    private String serverName;
    private String clientName;

    private boolean connected = false;

    private final List<ImageClientListener> imageClientListenerList = new ArrayList<>();

    private ClientCommunicationThread communicationThread;

    public Client(String serverName, String clientName) {
        this.serverName = serverName;
        this.clientName = clientName;
    }

    public String getClientName() {
        if (clientName == null) {
            clientName = "";
        }
        return clientName;
    }

    public boolean isConnected() {
        return connected;
    }

    public void close() {
        try {
            disconnectClient(true);
        } catch (Exception e) {
            //
        }
    }

    public void addImageClientListener(ImageClientListener clientListener) {
        if (!imageClientListenerList.contains(clientListener)) {
            imageClientListenerList.add(clientListener);
        }
    }

    public void removeImageClientListener(ImageClientListener clientListener) {
        imageClientListenerList.remove(clientListener);
    }

    public void onClientConnected(String clientName) {
        for (ImageClientListener listener : imageClientListenerList) {
            listener.onConnected(clientName);
        }
    }

    public void onClientDisconnected(String clientName) {
        for (ImageClientListener listener : imageClientListenerList) {
            listener.onDisconnected(clientName);
        }
    }

    public void onImageReceived(BufferedImage image, String imageName, ImageType imageType) {
        for (ImageClientListener listener : imageClientListenerList) {
            listener.onImageReceived(image, imageName, imageType);
        }
    }

    public void onImageTransmitted(String imageName, ImageType imageType) {
        for (ImageClientListener listener : imageClientListenerList) {
            listener.onImageTransmitted(imageName, imageType);
        }
    }

    public BufferedImage sendImage(File file, ImageType imageType, String name) throws Exception {
        BufferedImage image = null;
        if (file.exists()) {
            image = ImageUtils.convertImage(file, imageType);
            if (image != null) {
                if (name == null || name.isEmpty()) {
                    name = file.getName();
                }
                name = name.split("\\.", 2)[0]; // Remove everything after '.'
                sendImage(image, name, imageType);
            }
        }
        return image;
    }

    public void sendImage(final BufferedImage image, final String name, final ImageType imageType) {

        if (image != null && name != null && imageType != null) {
            final String message = clientName + "," + imageType.getId() + "," + name;
            SocketMessage sendImageMessage = new SocketMessage(SocketCommand.SendImage, message, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {

                    if (socketMessage.isValid()) {
                        try {
                            int port = Integer.valueOf(socketMessage.getMessage());
                            try (Socket client = new Socket(serverName, port)) {
                                client.setSoTimeout(2000);
                                // Send image
                                ImageIO.write(image, "JPG", client.getOutputStream());
                            }
                            onImageTransmitted(name, imageType);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Invalid message received while sending image " + name);
                    }
                }
            });
            communicationThread.sendMessage(sendImageMessage);
        }
    }

    public void getImage(final String name, final ImageType imageType) {
        if (name != null && imageType != null) {
            final String message = clientName + "," + imageType.getId() + "," + name;
            SocketMessage getImageMessage = new SocketMessage(SocketCommand.GetImage, message, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {
                    BufferedImage image = null;
                    if (socketMessage.isValid()) {
                        try {
                            int port = Integer.valueOf(socketMessage.getMessage());
                            try (Socket client = new Socket(serverName, port)) {
                                // Send image
                                image = ImageIO.read(ImageIO.createImageInputStream(client.getInputStream()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (image != null && image.getWidth() > 1) {
                        onImageReceived(image, name, imageType);
                    } else {
                        onImageReceived(null, name, imageType);
                    }

                }
            });
            communicationThread.sendMessage(getImageMessage);
        }
    }

    public void connectClient(final String clientName) {
        if (clientName != null) {

            if (communicationThread == null) {
                communicationThread = new ClientCommunicationThread(serverName, msPort);
                communicationThread.start();
            }

            SocketMessage socketMessage = new SocketMessage(SocketCommand.ConnectClient, clientName, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {
                    connected = socketMessage != null && socketMessage.isValid();
                    onClientConnected(clientName);
                }
            });
            communicationThread.sendMessage(socketMessage);
        }
    }

    public void disconnectClient(final boolean closeDown) {
        if (clientName != null) {
            SocketMessage socketMessage = new SocketMessage(SocketCommand.DisconnectClient, clientName, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {
                    if (socketMessage.isValid()) {
                        connected = false;
                        onClientDisconnected(clientName);
                    }
                    if (closeDown) {
                        communicationThread.stopRunning();
                    }
                }
            });
            communicationThread.sendMessage(socketMessage);
        }
    }
}
