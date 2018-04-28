package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.SocketMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ClientCommunicationThread extends Thread {

    private final MessageQueue messageQueue = new MessageQueue(100);
    private String serverName;
    private int port;

    private volatile boolean running;

    public ClientCommunicationThread(String serverName, int port) {
        this.serverName = serverName;
        this.port = port;

    }

    public synchronized void stopRunning() {
        messageQueue.stop();
        running = false;
    }

    public synchronized void sendMessage(SocketMessage message) {
        if (message != null && running) {
            try {
                messageQueue.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
    }

    @Override
    public void run() {

        try {
            try(Socket clientSocket = new Socket(serverName, port);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {

                while (running) {
                    try {
                        SocketMessage message = messageQueue.take();
                        if (message != null) {

                            // Send message
                            out.println(message.toString());

                            // Wait for server
                            String input = in.readLine();
                            message.response(SocketMessage.convert(input));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Closing down client communication thread");

    }


    private class MessageQueue {

        private final Queue<SocketMessage> queue = new LinkedList<>();
        private final int capacity;
        private volatile boolean stopped;

        MessageQueue(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void put(SocketMessage message) throws InterruptedException {
            while (size() >= capacity) {
                wait();
            }

            if (!stopped) {
                queue.add(message);
                notifyAll();
            }
        }

        public synchronized SocketMessage take() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }

            if (!stopped) {
                SocketMessage message = queue.remove();
                notifyAll();
                return message;
            }
            return null;
        }

        public synchronized void stop() {
            stopped = true;
            notifyAll();
        }

        public synchronized int size() {
            return queue.size();
        }

    }
}
