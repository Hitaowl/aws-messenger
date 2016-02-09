package de.fhbrandenburg.ristpr.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fprus on 30.01.2016.
 */
public class ConSettings {
    JFrame settingsFrame;
    JPanel settingsPanel;
    JButton buttonConnect;
    JLabel nameLabel;
    JLabel adressLabel;
    JLabel portLabel;
    JTextField nameField;
    JTextField idField;
    JTextField portField;
    Client client = new Client();

    public static void main(String[] args) {
        ConSettings s = new ConSettings();
        s.createGUI();
    }

    public void createGUI() {
        settingsFrame = new JFrame("Settings");
        settingsFrame.setSize(320, 130);

        settingsPanel = new JPanel(null);

        //Buttons
        buttonConnect = new JButton("Connect");
        buttonConnect.addActionListener(new SendButtonListener());

        //Labels
        nameLabel = new JLabel("Name:");
        adressLabel = new JLabel("IP-Adresse:");
        portLabel = new JLabel("Port:");

        //Fields
        nameField = new JTextField(15);
        idField = new JTextField(15);
        portField = new JTextField(5);
        portField.setText("4444");

        settingsFrame.getContentPane().add(BorderLayout.CENTER, settingsPanel);

        settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        settingsFrame.setVisible(true);


        //add
        settingsPanel.add(nameLabel);
        settingsPanel.add(nameField);
        settingsPanel.add(adressLabel);
        settingsPanel.add(idField);
        settingsPanel.add(portLabel);
        settingsPanel.add(portField);
        settingsPanel.add(buttonConnect);
        nameLabel.setBounds(5, 5, 40, 20);
        nameField.setBounds(75, 5, 200, 20);
        adressLabel.setBounds(5, 30, 70, 20);
        idField.setBounds(75, 30, 200, 20);
        portLabel.setBounds(5, 55, 50, 20);
        portField.setBounds(75, 55, 70, 20);
        buttonConnect.setBounds(183, 55, 90, 20);
    }

    public class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                client.createGUI(idField.getText(), portField.getText(), nameField.getText());
            } catch (IOException ex) {
                Logger.getLogger(ConSettings.class.getName()).log(Level.SEVERE, null, ex);
            }
            settingsFrame.dispose();

        }

    }
}
