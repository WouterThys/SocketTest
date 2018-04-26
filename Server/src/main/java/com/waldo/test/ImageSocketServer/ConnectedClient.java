package com.waldo.test.ImageSocketServer;

import com.waldo.test.Main;

import java.util.Objects;

public class ConnectedClient {

    private String name;
    private ReceiveThread receiveThread;
    private TransmitThread transmitThread;

    public ConnectedClient(String name) throws Exception {
        this.name = name;

        receiveThread = new ReceiveThread(Main.root);
        transmitThread = new TransmitThread(Main.root);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectedClient)) return false;
        ConnectedClient that = (ConnectedClient) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public void start() {
        try {
            receiveThread.start();
            transmitThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            transmitThread.stopRunning();
            receiveThread.stopRunning();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    public int getReceivePort() {
        int port = -1;

        if (receiveThread != null && receiveThread.isAlive()) {
            port = receiveThread.getPort();
        }

        return port;
    }

    public int getTransmitPort() {
        int port = -1;

        if (transmitThread != null && transmitThread.isAlive()) {
            port = transmitThread.getPort();
        }

        return port;
    }


}
