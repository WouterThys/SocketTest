package com.waldo.test.ImageSocketServer;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

abstract class SocketThread extends Thread {

    private final static Logger logger = Logger.getLogger(SocketThread.class);

    ServerSocket serverSocket;
    private volatile boolean running;
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
        this.interrupt();
    }

    abstract void doInBackground() throws IOException;

    @Override
    public void run() {
        try {
            while (running) {
                try {
                    doInBackground();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.debug("Thread for socket port " + getPort() + " closed");
    }
}
