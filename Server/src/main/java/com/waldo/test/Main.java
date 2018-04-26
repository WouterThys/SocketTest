package com.waldo.test;

import com.waldo.test.ImageSocketServer.ReceiveThread;
import com.waldo.test.ImageSocketServer.TransmitThread;

public class Main {

    private static final String RootParam = "RootDir";
    private static final String TxPortParam = "TxPort";
    private static final String RxPortParam = "RxPort";

    // Defaults
    private static String root = "/home/wouter/Desktop/ImageTest/";
    private static int rxPort = 12321;
    private static int txPort = 32123;

    public static void main(String[] args) throws Exception {

        readArgs(args);

        ReceiveThread receiveThread = new ReceiveThread(rxPort, root);
        receiveThread.start();

        TransmitThread transmitThread = new TransmitThread(txPort, root);
        transmitThread.start();
    }

    private static void readArgs(String[] args) {
        if (args != null && args.length != 0) {
            for (String param : args) {
                String[] split = param.split("=");
                if (split.length == 2) {
                    switch (split[0]) {
                        case RootParam:
                            if (!split[1].isEmpty()) {
                                root = split[1];
                            }
                            break;
                        case TxPortParam:
                            if (!split[1].isEmpty()) {
                                txPort = Integer.valueOf(split[1]);
                            }
                            break;
                        case RxPortParam:
                            if (!split[1].isEmpty()) {
                                rxPort = Integer.valueOf(split[1]);
                            }
                            break;
                    }
                }
            }
        }
    }
}
