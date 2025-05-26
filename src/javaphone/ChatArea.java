/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author bafc13
 */
public class ChatArea extends javax.swing.JPanel {
    private JPanel chatPanel;
    private JTextArea chatArea;
    private JScrollPane chatPane;
    private JTextField inputField;
    private JTextArea userArea;
    private JScrollPane userPane;


    public ChatArea (Dimension screenSize, boolean isCall) {
        super();

        if(isCall == false) {
            this.setSize(900,300);
            chatPanel = new JPanel();
            chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
            chatPanel.setMinimumSize(new Dimension(500, 300));
            chatPanel.setPreferredSize(new Dimension(500,300));
            chatPanel.setMaximumSize(new Dimension(500,300));
            chatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


            chatArea = new JTextArea("");
            chatArea.setEditable(false);
            chatArea.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);

            chatPane = new JScrollPane(chatArea);
            chatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            chatPane.setMinimumSize(new Dimension(500, 270));
            chatPane.setPreferredSize(new Dimension(500,270));
            chatPane.setMaximumSize(new Dimension(500,270));
            chatPane.setBorder(new RoundedBorder(5));

            inputField = new JTextField();
            inputField.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
            inputField.addActionListener(e -> messageWritten());

            chatPane.setAlignmentX(Component.CENTER_ALIGNMENT);


            chatPanel.add(chatPane,BorderLayout.CENTER);
            chatPanel.add(inputField);



            userArea = new JTextArea("");
            userArea.setEditable(false);
            userArea.setFont(new Font("Arial Unicode MS", Font.BOLD, 20));
            userArea.setLineWrap(true);
            userArea.setWrapStyleWord(true);

            userPane = new JScrollPane(userArea);
            userPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            userPane.setMinimumSize(new Dimension(360, 300));
            userPane.setPreferredSize(new Dimension(360, 300));
            userPane.setMaximumSize(new Dimension(360, 300));
            userArea.append("bafc13\n");
            userArea.setAlignmentX(Component.CENTER_ALIGNMENT);
            userPane.setBorder(new RoundedBorder(5));
        } else {
            if(chatPanel != null) {
                this.remove(chatPanel);
                this.remove(userPane);
            }

            this.setSize(900,300);
            chatPanel = new JPanel();
            chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
            chatPanel.setMinimumSize(new Dimension(500, 300));
            chatPanel.setPreferredSize(new Dimension(500,300));
            chatPanel.setMaximumSize(new Dimension(500,300));
            chatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);



            chatArea = new JTextArea("");
            chatArea.setEditable(false);
            chatArea.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));
            chatArea.setLineWrap(true);
            chatArea.setWrapStyleWord(true);
            chatPane = new JScrollPane(chatArea);
            chatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            chatPane.setMinimumSize(new Dimension(500, 270));
            chatPane.setPreferredSize(new Dimension(500,270));
            chatPane.setMaximumSize(new Dimension(500,270));
            chatPane.setBorder(new RoundedBorder(5));

            inputField = new JTextField();
            inputField.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
            inputField.addActionListener(e -> messageWritten());

            chatPane.setAlignmentX(Component.CENTER_ALIGNMENT);

            chatPanel.add(chatPane,BorderLayout.CENTER);
            chatPanel.add(inputField);


            userArea = new JTextArea("");
            userArea.setEditable(false);
            userArea.setFont(new Font("Arial Unicode MS", Font.BOLD, 20));
            userArea.setLineWrap(true);
            userArea.setWrapStyleWord(true);
            userPane = new JScrollPane(userArea);
            userPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            userPane.setMinimumSize(new Dimension(360, 300));
            userPane.setPreferredSize(new Dimension(360, 300));
            userPane.setMaximumSize(new Dimension(360, 300));
            userArea.append("bafc13\n");
            userArea.setAlignmentX(Component.CENTER_ALIGNMENT);
            userPane.setBorder(new RoundedBorder(5));
        }


        this.add(chatPanel);
        this.add(userPane);
    }

    private void messageWritten(){
        if(inputField.getText() != "") {
            chatArea.append(inputField.getText() + "\n");
            inputField.setText("");
        }
    }

}
