package com.waldo.test.ImageSocketServer;

import java.io.IOException;
import java.net.ServerSocket;

abstract class SocketThread extends Thread {

    ServerSocket serverSocket;
    private boolean running;
    private int port = -1;

    SocketThread(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.running = true;
    }

    public int getPort() {
        if (port < 0) {
            port = 0;
            if (serverSocket != null) {
                port = serverSocket.getLocalPort();
            }
        }
        return port;
    }

    public synchronized void stopRunning() {
        running = false;
    }

    abstract void doInBackground() throws IOException;

    public void run() {
        while (running) {
            try {
                doInBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Thread for socket port " + getPort() + " closed");
    }
}
