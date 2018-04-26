package com.waldo.test.client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

    private String serverName;
    private int txPort;
    private int rxPort;

    public Client(String serverName, int txPort, int rxPort) {
        this.serverName = serverName;
        this.txPort = txPort;
        this.rxPort = rxPort;
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

    public void send(File file) throws Exception {
        if (file.exists()) {
            BufferedImage image = convertImage(file);
            if (image != null) {
                String name = file.getName();
                name = name.split("\\.", 2)[0]; // Remove everything after '.'
                send(image, name);
            }
        }
    }

    public void send(BufferedImage image, String name) throws Exception {
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

    public BufferedImage get(String name) throws Exception {
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
        return image;
    }
}
