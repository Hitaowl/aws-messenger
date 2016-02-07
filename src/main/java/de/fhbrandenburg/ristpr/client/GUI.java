/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhbrandenburg.ristpr.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author taake
 */
public class GUI {
 
    
    private JFrame framePic;
    JScrollPane scrollPane;
    
    public void constructFrame() throws MalformedURLException, IOException{
        
        
        framePic = new JFrame("pic");
        framePic.setLayout(null);
        JButton btn = new JButton("Add Pic");
        
        scrollPane = new JScrollPane();
        JLabel picLabel = new JLabel();
        btn.setBounds(25, 740, 350, 50);
        picLabel.setBounds(10, 10, 380, 720);
        URL url = new URL("https://s3.amazonaws.com/awsimagefhb2/image25712523.jpg");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    S3Conn.picUpload(getURL());
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
        
        BufferedImage img = ImageIO.read(url);
        img.getScaledInstance(380, 720, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        picLabel.setIcon(icon);
        
        scrollPane.add(picLabel);
        
      
        framePic.add(btn);
        framePic.add(picLabel);
        framePic.setSize(400, 800);
        framePic.setVisible(true);
        
    }
    
    public String getURL(){
        String URL;
        JFrame fileChooserFrame = new JFrame("get picture");
        JFileChooser fileChooser = new JFileChooser();
        fileChooserFrame.setVisible(true);
        fileChooserFrame.getContentPane().add(fileChooser);
        URL = fileChooser.getSelectedFile().getAbsolutePath();
        fileChooserFrame.setSize(500, 500);
        fileChooserFrame.setVisible(false);
        return URL;
        
        
    
        
    }    
    
    public void gotPic(String link) throws MalformedURLException, IOException{
    
    URL url = new URL(link);
    BufferedImage img = ImageIO.read(url);
    img.getScaledInstance(380, 720, Image.SCALE_SMOOTH);
    ImageIcon icon = new ImageIcon(img);
    JLabel picLabel = new JLabel();
    picLabel.setIcon(icon);
    scrollPane.add(picLabel);
    framePic.validate();
    framePic.repaint();
}
    
    
    
}
