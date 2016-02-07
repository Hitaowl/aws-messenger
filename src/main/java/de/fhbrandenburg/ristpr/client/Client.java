package de.fhbrandenburg.ristpr.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    JFrame clientFrame;
    JPanel clientPanel;
    JTextArea textArea_Messages;
    JTextField textField_ClientMessage;
    JButton button_SendMessage;
    JTextField textField_Username;
    JScrollPane scrollPane_Messages;

    Socket client;
    PrintWriter writer;
    BufferedReader reader;
    String nick;

    public void createGUI(String ip, String port, String nick) throws IOException {
        this.nick = nick;
        
        GUI gui = new GUI();
        gui.constructFrame();

        clientFrame = new JFrame("");
        clientFrame.setSize(800, 600);

        // Panel erzeugen, welches alle anderen Inhalte enthält
        clientPanel = new JPanel();

        textArea_Messages = new JTextArea();
        textArea_Messages.setEditable(false);

        textField_ClientMessage = new JTextField(38);
        textField_ClientMessage.addKeyListener(new SendPressEnterListener());

        button_SendMessage = new JButton("Senden");
        button_SendMessage.addActionListener(new SendButtonListener());

        textField_Username = new JTextField(10);
        textField_Username.setText(nick);
        textField_Username.setEditable(false);

        // Scrollbalken zur textArea hinzufügen
        scrollPane_Messages = new JScrollPane(textArea_Messages);
        scrollPane_Messages.setPreferredSize(new Dimension(700, 500));
        scrollPane_Messages.setMinimumSize(new Dimension(700, 500));
        scrollPane_Messages.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane_Messages.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        if (!connectToServer(ip, port)) {
            // Connect-Label anzeigen ob verbunden oder nicht...
        }

        Thread t = new Thread(new MessagesFromServerListener());
        t.start();

        clientPanel.add(scrollPane_Messages);
        clientPanel.add(textField_Username);
        clientPanel.add(textField_ClientMessage);
        clientPanel.add(button_SendMessage);
        
        // Panel zum ContentPane (Inhaltsbereich) hinzufügen
        clientFrame.getContentPane().add(BorderLayout.CENTER, clientPanel);
        clientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                sendCommend("QUIT " + getNick());
            }
        });
        clientFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientFrame.setVisible(true);
    }

    public boolean connectToServer(String ip, String port) {
        try {
            client = new Socket(ip, Integer.parseInt(port));
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream());
            appendTextMessages("Netzwerkverbindung hergestellt");
            sendCommend("NICK " + this.nick);

            return true;
        } catch (Exception e) {
            appendTextMessages("Netzwerkverbindung konnte nicht hergestellt werden");
            e.printStackTrace();

            return false;
        }
    }

    public void sendCommend(String message) {
        writer.println(message);
        writer.flush();
    }

    public void sendMessageToServer() {
        writer.println("PRIVMSG #default :" + textField_ClientMessage.getText());
        writer.flush();

        textField_ClientMessage.setText("");
        textField_ClientMessage.requestFocus();
    }

    public void appendTextMessages(String message) {
        textArea_Messages.append(message + "\n");
    }
    
    public String getNick(){
        return nick;
    }

    // Listener
    public class SendPressEnterListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent arg0) {
            if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                sendMessageToServer();
            }
        }

        @Override
        public void keyReleased(KeyEvent arg0) {
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
        }

    }

    public class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessageToServer();
        }

    }

    public class MessagesFromServerListener implements Runnable {

        @Override
        public void run() {
            String message;

            try {
                while ((message = reader.readLine()) != null) {
                    if (message.startsWith("PING")) {
                        sendCommend("PONG " + nick);

                    } else if (message.contains("NOTICE") && !message.contains("PRIVMSG") && !message.contains("LINK")) {
                        appendTextMessages(message.substring(message.indexOf(":", 1) + 1));
                        textArea_Messages.setCaretPosition(textArea_Messages.getText().length());

                    } else if (message.contains("JOIN") && !message.contains("PRIVMSG")) {
                        appendTextMessages("Sie befinden sich nun im Channel " + message.substring(message.lastIndexOf("#") - 1));
                        textArea_Messages.setCaretPosition(textArea_Messages.getText().length());
                    } else if (message.contains("PRIVMSG")) {
                        appendTextMessages(message.split(" ")[0].substring(1, message.split(" ")[0].length()-1) + ": " +
                                message.substring(message.indexOf(":", 1) + 1));
                        textArea_Messages.setCaretPosition(textArea_Messages.getText().length());
                    } else if (message.contains("NOTICE") && message.contains("LINK") && !message.contains("PRIVMSG")) {
                        //hiel kommen linkst auf message.substring(message.indexof(":",1)).split(" ")[1]
                        //!!! nicht getestet
                    } else if (message.contains("LINK")) {
                        GUI gui = new GUI();                        
                        gui.gotPic(message.split(" ")[0].substring(1, message.split(" ")[0].length()-1) + ": " +
                                message.substring(message.indexOf(":", 1) + 1));
                    }
                    
                }
            } catch (
                    IOException e
                    )

            {
                appendTextMessages("Nachricht konnte nicht empfangen werden!");
                e.printStackTrace();
            }
        }

    }
}
