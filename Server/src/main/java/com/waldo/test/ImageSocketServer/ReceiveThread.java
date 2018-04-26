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
    void doInBackground() {
        try {
            Socket server = serverSocket.accept();

            BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));

            String imageInfo = br.readLine();
            BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(server.getInputStream()));

            String[] split = imageInfo.split(";");
            String typeStr = split[0];
            String name = split[1];

            ImageType type = ImageType.fromInt(Integer.valueOf(typeStr));

            SaveImageThread saveImageThread = new SaveImageThread(img, name, type);
            saveImageThread.start();

            // Response
            OutputStreamWriter out = new OutputStreamWriter(server.getOutputStream());
            SocketMessage response = new SocketMessage(SocketCommand.ConnectClient, "OK");

            String output = response.toString() + "\n";
            out.write(output, 0, output.length());
            out.flush();

            // Close
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
    }
}
