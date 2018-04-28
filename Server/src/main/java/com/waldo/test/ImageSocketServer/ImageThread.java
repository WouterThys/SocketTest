package com.waldo.test.ImageSocketServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

abstract class ImageThread extends Thread {

    public interface OnThreadDoneListener {
        void done(ImageThread thread);
    }

    private OnThreadDoneListener threadDoneListener;
    private ServerSocket serverSocket;
    private String imageName;
    private ImageType imageType;

    ImageThread(String imageName, ImageType imageType, OnThreadDoneListener threadDoneListener) throws IOException {
        this.serverSocket = new ServerSocket(0);
        this.serverSocket.setSoTimeout(5000);
        this.imageName = imageName;
        this.imageType = imageType;
        this.threadDoneListener = threadDoneListener;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageThread)) return false;
        ImageThread that = (ImageThread) o;
        return getPort() == that.getPort();
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageName);
    }

    abstract void doInBackground(Socket clientSocket, String imageName, ImageType imageType);

    @Override
    public void run() {
        try (Socket clientSocket = serverSocket.accept()) {

            doInBackground(clientSocket, imageName, imageType);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (threadDoneListener != null) {
            threadDoneListener.done(this);
        }
    }
}
