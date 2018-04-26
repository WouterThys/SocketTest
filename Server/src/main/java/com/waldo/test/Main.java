package com.waldo.test;

import com.waldo.test.ImageSocketServer.ConnectedClient;
import com.waldo.test.ImageSocketServer.MessageThread;
import com.waldo.test.ImageSocketServer.SocketMessage;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String RootParam = "RootDir";

    // Defaults
    public static String rootDirectory = "/home/wouter/Desktop/ImageTest/";
    private final static int msPort = 31213; // Message port

    private static final List<ConnectedClient> connectedClients = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        readArgs(args);
        System.out.println("Starting image server");

        final MessageThread messageThread = new MessageThread(msPort, new MessageThread.MessageListener() {
            @Override
            public SocketMessage onNewMessage(SocketMessage message) {
                System.out.println("Got: " + message);

                SocketMessage result = new SocketMessage(message.getCommand(), "");
                switch (message.getCommand()) {
                    case ConnectClient:
                        ConnectedClient connectClient = connectClient(message.getMessage());
                        if (connectClient != null) {
                            result.setMessage(connectClient.getReceivePort() + "," + connectClient.getTransmitPort());
                            System.out.println("Connected client: " + connectClient.getName());
                        }
                        break;
                    case DisconnectClient:
                        ConnectedClient disconnectClient = disconnectClient(message.getMessage());
                        if (disconnectClient != null) {
                            result.setMessage(disconnectClient.getName());
                            System.out.println("Disconnected client: " + disconnectClient.getName());
                        }
                        break;
                }

                return result;
            }
        });
        messageThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    messageThread.stopRunning();
                    for (ConnectedClient client : connectedClients) {
                        client.close();
                    }
                } catch (Exception e) {
                    //
                }
            }
        }));
    }

    private static ConnectedClient connectClient(String name) {
        ConnectedClient client = findClient(name);
        if (client == null) {
            try {
                client = new ConnectedClient(name);
                client.start();

                connectedClients.add(client);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    private static ConnectedClient disconnectClient(String name) {
        ConnectedClient client = findClient(name);
        if (client != null) {
            client.close();

            connectedClients.remove(client);
        }
        return client;
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
