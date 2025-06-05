/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.Color;
import java.awt.Component;
import java.util.Timer;
import java.util.TimerTask;
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
    public static final String userStatusOnline = "В сети";
    public static final String userStatusOffline = "Не в сети";
    public static final String serverStatusOnline = "Хост";
    public static final String serverStatusOffline = "Не хост";

    public static final long refreshRate = 5000L;
    
    public String ip;
    public String username;

    private JLabel nickname;
    private JLabel status;
    private JLabel isHost;
    private JButton connectButton;
    private JButton messageButton;
    private Timer refreshTimer;
    private TimerTask pingTask;

    public FriendPanel(String ip, String username) {
        super();

        this.ip = ip;
        this.username = username;

        pingTask = new TimerTask() {
            public void run() {
                ping();
            }
        ;
        };
        refreshTimer = new Timer();
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        nickname = new JLabel(username);
        nickname.setBorder(new RoundedBorder(3));
        status = new JLabel(userStatusOffline);
        status.setForeground(Color.red);
        status.setBorder(new RoundedBorder(3));
        isHost = new JLabel(serverStatusOffline);
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
        refreshTimer.schedule(pingTask, refreshRate, refreshRate);
    }

    private void connect() {
        System.out.println("Not supported yet");
    }

    private void message() {
        System.out.println("MESSAGING FRIEND");
        mainJFrame.mainSock.call(ip, mainJFrame.username, CallCodes.callDM);
    }

    private void ping() {
        mainJFrame.mainSock.call(ip, mainJFrame.username, CallCodes.callPing);
    }

    public void refresh(Boolean userStatus, Boolean serverStatus, String username) {
        if (userStatus) {
            status.setText(userStatusOnline);
            status.setForeground(Color.green);
        } else {
            status.setText(userStatusOffline);
            status.setForeground(Color.red);
        }
        if (serverStatus) {
            isHost.setText(serverStatusOnline);
            isHost.setForeground(Color.green);
        } else {
            isHost.setText(serverStatusOffline);
            isHost.setForeground(Color.red);
        }

        if (!username.equals("")) {
            this.username = username;
            nickname.setText(username);
        }
    }
}
