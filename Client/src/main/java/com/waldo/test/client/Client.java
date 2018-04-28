package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.ImageSocketServer.SocketCommand;
import com.waldo.test.ImageSocketServer.SocketMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Client {

    public interface OnImageReceiveListener {
        void onImageReceived(BufferedImage image);
    }

    private static final int msPort = 31213;
    private String serverName;
    private String clientName;

    private ClientCommunicationThread communicationThread;

    public Client(String serverName, String clientName) {
        this.serverName = serverName;
        this.clientName = clientName;

        connectClient(clientName);
    }

    public String getClientName() {
        if (clientName == null) {
            clientName = "";
        }
        return clientName;
    }

    public void close() {
        try {
            disconnectClient(true);
        } catch (Exception e) {
            //
        }
    }

    public void sendImage(File file, ImageType imageType) throws Exception {
        if (file.exists()) {
            BufferedImage image = ImageUtils.convertImage(file, imageType);
            if (image != null) {
                String name = file.getName();
                name = name.split("\\.", 2)[0]; // Remove everything after '.'
                sendImage(image, name, imageType);
            }
        }
    }

    public void sendImage(final BufferedImage image, String name, ImageType imageType) {

        if (image != null && name != null && imageType != null) {
            final String message = clientName + "," + imageType.getId() + "," + name;
            SocketMessage sendImageMessage = new SocketMessage(SocketCommand.SendImage, message, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {

                    if (socketMessage.isValid()) {
                        try {
                            int port = Integer.valueOf(socketMessage.getMessage());
                            try(Socket client = new Socket(serverName, port)) {
                                // Send image
                                ImageIO.write(image, "JPG", client.getOutputStream());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            communicationThread.sendMessage(sendImageMessage);
        }
    }

    public void getImage(String name, ImageType imageType, final OnImageReceiveListener listener) {
        if (name != null && imageType != null) {
            final String message = clientName + "," + imageType.getId() + "," + name;
            SocketMessage getImageMessage = new SocketMessage(SocketCommand.GetImage, message, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {
                    BufferedImage image = null;
                    if (socketMessage.isValid()) {
                        try {
                            int port = Integer.valueOf(socketMessage.getMessage());
                            try(Socket client = new Socket(serverName, port)) {
                                // Send image
                                image = ImageIO.read(ImageIO.createImageInputStream(client.getInputStream()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (listener != null) {
                        if (image != null && image.getWidth() > 1) {
                            listener.onImageReceived(image);
                        } else {
                            listener.onImageReceived(null);
                        }
                    }
                }
            });
            communicationThread.sendMessage(getImageMessage);
        }
    }

    private void connectClient(String clientName) {
        if (clientName != null) {

            if (communicationThread == null) {
                communicationThread = new ClientCommunicationThread(serverName, msPort);
                communicationThread.start();
            }

            SocketMessage socketMessage = new SocketMessage(SocketCommand.ConnectClient, clientName, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {
                    System.out.println("Server: " + socketMessage);
                }
            });
            communicationThread.sendMessage(socketMessage);
        }
    }

    private void disconnectClient(final boolean closeDown) {
        if (clientName != null) {
            SocketMessage socketMessage = new SocketMessage(SocketCommand.DisconnectClient, clientName, new SocketMessage.OnResponseListener() {
                @Override
                public void onResponse(SocketMessage socketMessage) {
                    if (closeDown) {
                        communicationThread.stopRunning();
                    }
                }
            });
            communicationThread.sendMessage(socketMessage);
        }
    }
}
