package com.waldo.test.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Application extends JFrame implements ActionListener {

    private JLabel imageLabel;

    private JButton sendBtn;
    private JButton selectImageBtn;
    private JButton getImageBtn;

    private Client client;

    public Application() {
        super();

        createGui();
        try {
            client = new Client("192.168.0.182", "Test");
            selectImageBtn.setEnabled(client.isConnected());
            getImageBtn.setEnabled(client.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createGui() {
        imageLabel = new JLabel();

        selectImageBtn = new JButton("Select image");
        selectImageBtn.addActionListener(this);

        getImageBtn = new JButton("Get");
        getImageBtn.addActionListener(this);

        sendBtn = new JButton("Command");
        sendBtn.addActionListener(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel();

        btnPanel.add(sendBtn);
        btnPanel.add(selectImageBtn);
        btnPanel.add(getImageBtn);

        mainPanel.add(imageLabel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.NORTH);

        add(mainPanel);
    }

    private void doSelectImage() {
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            if (file.exists()) {
                try {
                    client.sendImage(file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(
                        Application.this,
                        "Error reading file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void doGetImage() {
        String name = JOptionPane.showInputDialog(
                Application.this,
                "Enter the name of the image",
                "Get image"
        );

        if (name != null && !name.isEmpty()) {

            try {
                BufferedImage image = client.getImage(name);
                if (image != null) {
                    ImageIcon imageIcon = new ImageIcon(image);
                    imageLabel.setIcon(imageIcon);
                } else {
                    imageLabel.setIcon(null);
                    JOptionPane.showMessageDialog(
                            Application.this,
                            "No such image..",
                            ":(",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void doSend() {
//        String command = JOptionPane.showInputDialog(
//                Application.this,
//                "Enter the name of the image",
//                "Get image"
//        );
//
//        if (command != null && !command.isEmpty()) {
//            try {
//                client.connectClient(command);
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (client != null) {
            if (e.getSource().equals(selectImageBtn)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        doSelectImage();
                    }
                });
            } else if (e.getSource().equals(getImageBtn)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        doGetImage();
                    }
                });
            } else if (e.getSource().equals(sendBtn)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        doSend();
                    }
                });
            }
        }
    }
}
