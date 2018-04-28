package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.ImageType;
import com.waldo.test.ImageSocketServer.SocketCommand;
import com.waldo.test.ImageSocketServer.SocketMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Client {

    private static final int msPort = 31213;
    private String serverName;
    private String clientName;
    private int txPort = -1;
    private int rxPort = -1;

    private boolean busy = false;

    public Client(String serverName, String clientName) throws IOException {
        this.serverName = serverName;
        this.clientName = clientName;

        connectClient(clientName);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    disconnectClient();
                } catch (IOException e) {
                    //
                }
            }
        }));
    }

    public boolean isConnected() {
        return txPort > 0 && rxPort > 0;
    }

    public String getClientName() {
        if (clientName == null) {
            clientName = "";
        }
        return clientName;
    }

    public boolean isBusy() {
        return busy;
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

    public void sendImage(BufferedImage image, String name, ImageType imageType) throws IOException {
        try(Socket client = new Socket(serverName, txPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream())) {

            // Send info
            String imageInfo = imageType.getId() + ";" + name + "\n";
            out.write(imageInfo, 0, imageInfo.length());
            out.flush();

            // Send image
            ImageIO.write(image, "JPG", client.getOutputStream());

            // Receive
            String input = in.readLine();
            System.out.println("Server: " + input);
        }
    }

    public BufferedImage getImage(String name, ImageType imageType) throws Exception {
        busy = true;
        Socket client = null;
        OutputStreamWriter out = null;
        BufferedImage image;
        try {
            client = new Socket(serverName, rxPort);

            String imageInfo = imageType.getId() + ";" + name + "\n";
            out = new OutputStreamWriter(client.getOutputStream());
            out.write(imageInfo, 0, imageInfo.length());
            out.flush();

            image = ImageIO.read(ImageIO.createImageInputStream(client.getInputStream()));
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            busy = false;
        }
        if (image.getHeight() > 1 && image.getWidth() > 1) {
            return image;
        } else {
            return null;
        }
    }

    private void connectClient(String clientName) throws IOException {
        if (clientName != null) {

            SocketMessage socketMessage = new SocketMessage(SocketCommand.ConnectClient, clientName);

            Socket client = null;
            OutputStreamWriter out = null;
            BufferedReader in = null;
            try {
                client = new Socket(serverName, msPort);

                // Write message
                String message = socketMessage.toString() + "\n";
                out = new OutputStreamWriter(client.getOutputStream());
                out.write(message, 0, message.length());
                out.flush();

                // Receive message
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String input = in.readLine();
                System.out.println("Server answered: " + input);

                try {
                    SocketMessage result = SocketMessage.convert(input);
                    if (result.getCommand().equals(SocketCommand.ConnectClient)) {
                        String[] ports = result.getMessage().split(",");
                        txPort = Integer.valueOf(ports[0]);
                        rxPort = Integer.valueOf(ports[1]);
                    } else {
                        // Failed
                        throw new IOException("Failed to connect client");
                    }
                } catch (Exception e) {
                    throw new IOException("Failed to connect client", e);
                }

            } finally {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void disconnectClient() throws IOException {
        if (clientName != null) {

            SocketMessage socketMessage = new SocketMessage(SocketCommand.DisconnectClient, getClientName());

            Socket client = null;
            OutputStreamWriter out = null;
            BufferedReader in = null;
            try {
                client = new Socket(serverName, msPort);

                // Write message
                String message = socketMessage.toString() + "\n";
                out = new OutputStreamWriter(client.getOutputStream());
                out.write(message, 0, message.length());
                out.flush();

                // Receive message
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String input = in.readLine();
                System.out.println("Server answered: " + input);

            } finally {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
