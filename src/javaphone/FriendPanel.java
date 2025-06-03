/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.Color;
import java.awt.Component;
import static javaphone.mainJFrame.mainSock;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author bafc13
 */
public class FriendPanel extends javax.swing.JPanel {

    //обводку для FriendPanel и чуть дисты между элементами в панели

    private JLabel nickname;
    private JLabel status;
    private JLabel isHost;
    private JButton connectButton;
    private JButton messageButton;
    private MainSocket mainSock;

    public FriendPanel(MainSocket mainSock){
        super();
        this.mainSock = mainSock;

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        nickname = new JLabel("bafc13");
        nickname.setBorder(new RoundedBorder(3));
        status = new JLabel("В сети");
        status.setForeground(Color.green);
        status.setBorder(new RoundedBorder(3));
        isHost = new JLabel("Не хост");
        isHost.setForeground(Color.red);
        isHost.setBorder(new RoundedBorder(3));

        connectButton = new JButton("Позвонить");
        connectButton.addActionListener(e -> connect());
        connectButton.setBorder(new RoundedBorder(3));

        messageButton = new JButton("Написать");
        messageButton.addActionListener(e -> message());
        messageButton.setBorder(new RoundedBorder(3));

        this.add(nickname);
        this.add(status);
        this.add(isHost);
        this.add(connectButton);
        this.add(messageButton);

        this.setSize(600, 40);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void connect() {
        String ip = "212.21.21.21.2";
        String nick = "bafc13";

        //айпи с ником пиздим с бд и вызываем звонок
        System.out.println("CONNECTING TO FRIEND ZZZ...");
//        mainSock.call(ip, nick, CallCodes.callDM);
    }

    private void message() {
        String ip = "212.21.21.21.2";
        String nick = "bafc13";

        //айпи с ником пиздим с бд и вызываем звонок
        System.out.println("MESSEGING FRIEND");
//        mainSock.call(ip, nick, CallCodes.callDM);
    }

}
