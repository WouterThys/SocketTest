package com.waldo.test.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Application extends JFrame implements ActionListener {

    private JLabel imageLabel;
    private JButton selectImageBtn;
    private JButton getImageBtn;

    private Client client;

    public Application() {
        super();

        client = new Client("192.168.0.182", 12321, 32123);
        createGui();
    }


    private void createGui() {

        imageLabel = new JLabel();
        selectImageBtn = new JButton("Select image");
        selectImageBtn.addActionListener(this);

        getImageBtn = new JButton("Get");
        getImageBtn.addActionListener(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel();

        btnPanel.add(selectImageBtn);
        btnPanel.add(getImageBtn);

        mainPanel.add(imageLabel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.NORTH);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (client != null) {
            if (e.getSource().equals(selectImageBtn)) {
                JFileChooser fc = new JFileChooser();
                int result = fc.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    if (file.exists()) {
                        try {
                            client.send(file);
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
            } else if (e.getSource().equals(getImageBtn)) {
                String name = JOptionPane.showInputDialog(
                        Application.this,
                        "Enter the name of the image",
                        "Get image"
                );

                if (name != null && !name.isEmpty()) {

                    try {
                        BufferedImage image = client.get(name);
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
        }
    }
}
