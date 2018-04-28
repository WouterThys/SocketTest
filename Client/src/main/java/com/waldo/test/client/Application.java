package com.waldo.test.client;

import com.waldo.test.ImageSocketServer.ImageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Application extends JFrame implements ActionListener, Client.OnImageReceiveListener {

    private JLabel imageLabel;

    private JButton sendAllBtn;
    private JButton selectImageBtn;
    private JButton getImageBtn;

    private Client client;

    public Application() {
        super();

        createGui();

        client = new Client("192.168.0.182", "Test");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                client.close();
            }
        }));
    }


    private void createGui() {
        imageLabel = new JLabel();

        selectImageBtn = new JButton("Send image");
        selectImageBtn.addActionListener(this);

        getImageBtn = new JButton("Get image");
        getImageBtn.addActionListener(this);

        sendAllBtn = new JButton("Send all");
        sendAllBtn.addActionListener(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel();

        btnPanel.add(selectImageBtn);
        btnPanel.add(getImageBtn);
        btnPanel.add(sendAllBtn);

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
                    client.sendImage(file, ImageType.ItemImage);
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
            client.getImage(name, ImageType.ItemImage, this);
        }
    }

    private void doSendAll() {
        File selectedFolder = selectFolder();
        if (selectedFolder != null) {
            SendFullContentTask task = new SendFullContentTask(client, new SendFullContentTask.SendFullContentListener() {
                @Override
                public void onError(Exception exception) {
                    System.out.println(exception);
                }

                @Override
                public void onUpdateState(int state, String description, String... args) {
                    System.out.println(description);
                }
            }, selectedFolder, ImageType.ItemImage);
            task.startReading();
        }
    }

    private File selectFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select a folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    @Override
    public void onImageReceived(BufferedImage image) {
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
            } else if (e.getSource().equals(sendAllBtn)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        doSendAll();
                    }
                });
            }
        }
    }
}
