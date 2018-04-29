package com.waldo.test.ImageSocketServer;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

public class ConnectedClient {

    private final static Logger logger = Logger.getLogger(ConnectedClient.class);

    private String name;
    private final Vector<ImageThread> imageThreads = new Vector<>();

    public ConnectedClient(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
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

    public int prepareReceive(String imageName, ImageType imageType) throws IOException {
        return addImageThread(new ReceiveThread(imageName, imageType, new ImageThread.OnThreadDoneListener() {
            @Override
            public void done(ImageThread thread) {
                removeImageThread(thread);
            }
        }));
    }

    public int prepareTransmit(String imageName, ImageType imageType) throws IOException {
        return addImageThread(new TransmitThread(imageName, imageType, new ImageThread.OnThreadDoneListener() {
            @Override
            public void done(ImageThread thread) {
                removeImageThread(thread);
            }
        }));
    }

    private synchronized int addImageThread(ImageThread thread) {
        int port = -1;
        if (thread != null) {
            if (!imageThreads.contains(thread)) {
                imageThreads.add(thread);
                thread.start();
                port = thread.getPort();

                logger.debug("Client " + name + " is running " + imageThreads.size() + " threads");
            }
        }
        return port;
    }

    private synchronized void removeImageThread(ImageThread thread) {
        if (thread != null) {
            imageThreads.remove(thread);
        }
    }

    public void close() {
        try {
//            for (ImageThread thread : imageThreads) {
//                thread.join();
//            }
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


}
