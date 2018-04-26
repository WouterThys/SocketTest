package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.SocketCommand;
import com.waldo.test.ImageSocketServer.SocketMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class Client {

    private static final int msPort = 31213;
    private String serverName;
    private String clientName;
    private int txPort = -1;
    private int rxPort = -1;

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

    private BufferedImage convertImage(File file) throws Exception {
        BufferedImage bufferedImage = null;
        if (file != null && file.exists()) {
            BufferedImage inputImage = ImageIO.read(file);
            if (inputImage != null) {
                Dimension dimension = getScaledDimension(inputImage.getWidth(), inputImage.getHeight(), new Dimension(256,256));

                Image tmp = inputImage.getScaledInstance(dimension.width, dimension.height, Image.SCALE_SMOOTH);
                BufferedImage scaled = new BufferedImage(dimension.width, dimension.height, inputImage.getType());

                Graphics2D g2d = scaled.createGraphics();
                g2d.drawImage(tmp, 0, 0, null);
                g2d.dispose();

                bufferedImage = new BufferedImage(scaled.getWidth(), scaled.getHeight(), BufferedImage.TYPE_INT_RGB);
                bufferedImage.createGraphics().drawImage(scaled, 0, 0, Color.WHITE, null);

            }
        }
        return bufferedImage;
    }

    private Dimension getScaledDimension(int originalWidth, int originalHeight, Dimension boundary) {

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

    public void sendImage(File file) throws Exception {
        if (file.exists()) {
            BufferedImage image = convertImage(file);
            if (image != null) {
                String name = file.getName();
                name = name.split("\\.", 2)[0]; // Remove everything after '.'
                sendImage(image, name);
            }
        }
    }

    public void sendImage(BufferedImage image, String name) throws Exception {
        Socket client = null;
        OutputStreamWriter out = null;
        try {
            client = new Socket(serverName, txPort);

            name = name + "\n";
            out = new OutputStreamWriter(client.getOutputStream());
            out.write(name, 0, name.length());
            out.flush();

            ImageIO.write(image, "JPG", client.getOutputStream());
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
        }
    }

    public BufferedImage getImage(String name) throws Exception {
        Socket client = null;
        OutputStreamWriter out = null;
        BufferedImage image;
        try {
            client = new Socket(serverName, rxPort);

            name = name + "\n";
            out = new OutputStreamWriter(client.getOutputStream());
            out.write(name, 0, name.length());
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
