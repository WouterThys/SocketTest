package com.waldo.test.client;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String [] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Application application = new Application();
                application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                application.setPreferredSize(new Dimension(400, 200));
                application.setLocationByPlatform(true);

                application.pack();
                application.setVisible(true);
            }
        });
    }
}
