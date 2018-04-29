package com.waldo.test;

import com.waldo.test.ImageSocketServer.CommunicationThread;
import com.waldo.test.ImageSocketServer.ConnectedClient;
import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.ImageSocketServer.SocketMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String RootParam = "RootDir";
    private final static Logger logger = Logger.getLogger(Main.class);

    // Defaults
    public static String rootDirectory = "/home/wouter/Desktop/ImageTest/";
    private final static int msPort = 31213; // Message port

    private static final List<ConnectedClient> connectedClients = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        readArgs(args);
        logger.debug("Starting image server");

        final CommunicationThread communicationThread = new CommunicationThread(msPort, new CommunicationThread.MessageListener() {
            @Override
            public SocketMessage onNewMessage(SocketMessage message) {

                SocketMessage result = new SocketMessage(message.getCommand(), "");
                switch (message.getCommand()) {

                    case ConnectClient:
                        ConnectedClient connectClient = clientConnect(message.getMessage());
                        if (connectClient != null) {
                            result.setMessage("Connected " + connectClient);
                            logger.debug("Connected client: " + connectClient.getName());
                        }
                        break;

                    case DisconnectClient:
                        ConnectedClient disconnectClient = clientDisconnect(message.getMessage());
                        if (disconnectClient != null) {
                            result.setMessage("Disconnected " + disconnectClient);
                            logger.debug("Disconnected client: " + disconnectClient.getName());
                        }
                        break;

                    case SendImage: {
                        String[] split = message.getMessage().split(",");
                        if (split.length == 3) {
                            String clientName = split[0];
                            String imageType = split[1];
                            String imageName = split[2];
                            ImageType type;
                            try {
                                type = ImageType.fromInt(Integer.valueOf(imageType));
                                int port = clientSendImage(clientName, imageName, type);
                                result.setMessage(String.valueOf(port));
                            } catch (Exception e) {
                                logger.error("Failed to send image", e);
                            }
                        }
                    }
                        break;

                    case GetImage: {
                        String[] split = message.getMessage().split(",");
                        if (split.length == 3) {
                            String clientName = split[0];
                            String imageType = split[1];
                            String imageName = split[2];
                            ImageType type;
                            try {
                                type = ImageType.fromInt(Integer.valueOf(imageType));
                                int port = clientGetImage(clientName, imageName, type);
                                result.setMessage(String.valueOf(port));
                            } catch (Exception e) {
                                logger.error("Failed to get image", e);
                            }
                        }
                    }
                        break;
                }

                return result;
            }
        });
        communicationThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communicationThread.stopRunning();
                    for (ConnectedClient client : connectedClients) {
                        client.close();
                    }
                    logger.debug("Stopped image server");
                } catch (Exception e) {
                    //
                }
            }
        }));
    }

    private static ConnectedClient clientConnect(String name) {
        ConnectedClient client = findClient(name);
        if (client == null) {
            try {
                client = new ConnectedClient(name);
                connectedClients.add(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    private static ConnectedClient clientDisconnect(String name) {
        ConnectedClient client = findClient(name);
        if (client != null) {
            client.close();
            connectedClients.remove(client);
        }
        return client;
    }

    private static int clientSendImage(String clientName, String imageName, ImageType imageType) throws IOException {
        int port = -1;
        ConnectedClient client = findClient(clientName);

        if (client != null) {
            port = client.prepareReceive(imageName, imageType);
        }

        return port;
    }

    private static int clientGetImage(String clientName, String imageName, ImageType imageType) throws IOException {
        int port = -1;
        ConnectedClient client = findClient(clientName);

        if (client != null) {
            port = client.prepareTransmit(imageName, imageType);
        }

        return port;
    }

    private static ConnectedClient findClient(String name) {
        if (name != null && !name.isEmpty()) {
            for (ConnectedClient client : connectedClients) {
                if (client.getName().equalsIgnoreCase(name)) {
                    return client;
                }
            }
        }
        return null;
    }

    private static void readArgs(String[] args) {
        if (args != null && args.length != 0) {
            for (String param : args) {
                String[] split = param.split("=");
                if (split.length == 2) {
                    switch (split[0]) {
                        case RootParam:
                            if (!split[1].isEmpty()) {
                                rootDirectory = split[1];
                            }
                            break;
                    }
                }
            }
        }
    }
}
