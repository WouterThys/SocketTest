package com.waldo.test.ImageSocketServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

class ReceiveThread extends SocketThread {

    ReceiveThread() throws IOException {
        super(0);
    }

    @Override
    void doInBackground() throws IOException {
        try(Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream())) {

            // Read image info
            String imageInfo = in.readLine();
            BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(clientSocket.getInputStream()));

            // Handle input
            String[] split = imageInfo.split(";");
            String typeStr = split[0];
            String name = split[1];
            ImageType type = ImageType.fromInt(Integer.valueOf(typeStr));

            // Save image
            SaveImageThread saveImageThread = new SaveImageThread(img, name, type);
            saveImageThread.start();

            // Response
            SocketMessage response = new SocketMessage(SocketCommand.ConnectClient, "OK");
            String output = response.toString() + "\n";
            out.write(output, 0, output.length());
            out.flush();

            System.out.println("Saved image " + name);
        }
    }
}
