/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaphone;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author bafc13
 */
public class SettingsFrame extends javax.swing.JFrame {

    //сделать подтягивание и изначальное задание никнейма (открытие окна с ником)

    private File file;
    private JTextField field1;
    private PrintWriter textFileWriter;

    public SettingsFrame() throws IOException {
        initComponents();
        this.setTitle("Call");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        JLabel label1 = new JLabel("Your nickname:");
        field1 = new JTextField();
        JButton okButton = new JButton("OK");

        okButton.addActionListener(e -> {
            writeSettings();
        });

        try {
            this.textFileWriter = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter("account.txt", false) // true для дописывания в файл
                    )
            );
        } catch (IOException e) {
            System.err.println("Ошибка при создании файла для записи: " + e.getMessage());
            throw e;
        }

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width / 8, screenSize.height / 7);


        setLayout(new GridLayout(2, 2, 5, 5));
        label1.setSize(100, 30);
        field1.setSize(100, 30);
        okButton.setSize(100, 30);
        this.add(label1);
        this.add(field1);
        this.add(okButton);

    }

    private void writeSettings() {
        if (textFileWriter != null || !textFileWriter.equals("")) {
            textFileWriter.println(field1.getText() + "\n");
            mainJFrame.username = field1.getText();
            textFileWriter.flush();
        }
        
        this.dispose();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(250, 150));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
