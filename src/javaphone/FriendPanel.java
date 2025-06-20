/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javaphone.MainWindow.mainSock;
import javax.swing.Box;
import javax.swing.Box.Filler;
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
    
    private ConnectionChecker cc;

    public FriendPanel(String ip, String username, Socket sock) {
        super();

        this.ip = ip;
        this.username = username;
        
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        nickname = new JLabel(username);
        nickname.setBorder(new RoundedBorder(3));
        nickname.setMaximumSize(new Dimension(200, 20));
        nickname.setMinimumSize(new Dimension(200, 20));
        
        status = new JLabel(userStatusOffline);
        status.setForeground(Color.red);
        status.setBorder(new RoundedBorder(3));
        status.setMaximumSize(new Dimension(100, 20));
        status.setMinimumSize(new Dimension(100, 20));
        
        isHost = new JLabel(serverStatusOffline);
        isHost.setForeground(Color.red);
        isHost.setBorder(new RoundedBorder(3));
        isHost.setMaximumSize(new Dimension(100, 20));
        isHost.setMinimumSize(new Dimension(100, 20));
        
        connectButton = new JButton("Позвонить");
        connectButton.addActionListener(e -> connect());
        connectButton.setBorder(new RoundedBorder(3));
        connectButton.setMaximumSize(new Dimension(100, 20));
        connectButton.setMinimumSize(new Dimension(100, 20));
        
        messageButton = new JButton("Написать");
        messageButton.addActionListener(e -> message());
        messageButton.setBorder(new RoundedBorder(3));
        messageButton.setMaximumSize(new Dimension(100, 20));
        messageButton.setMinimumSize(new Dimension(100, 20));
        
        // Filler filler = new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5));
        
        this.add(nickname);
        this.add(new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5)));
        this.add(status);
        this.add(new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5)));
        this.add(isHost);
        this.add(new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5)));
        this.add(connectButton);
        this.add(new Filler(new Dimension(5, 5), new Dimension(5, 5), new Dimension(5, 5)));
        this.add(messageButton);
        this.setBorder(new RoundedBorder(5));

        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (sock != null) {
            cc = new ConnectionChecker(sock, this);
            cc.start();
        }
    }

    private void connect() {
        System.out.println("Not supported yet");
    }

    private void message() {
        int chatID = MainWindow.db.getDmId(ip);
        if (chatID == -1) {
            System.out.println("WTF NO CHAT WITH USER " + username);
            return;
        }
        
        ConnectionInfo ci = MainWindow.connectionInfo.get(chatID);
        if (ci != null && ci.dm != null) {
            MainWindow.openChat(chatID);
        } else if (ci != null) {
            ci.toOpen = true;
            MainWindow.mainSock.call(ip, MainWindow.username, CallCodes.callDM);
        } else {
            MainWindow.connectionInfo.put(chatID, new ConnectionInfo(true));
            MainWindow.mainSock.call(ip, MainWindow.username, CallCodes.callDM);
        }
        
        
    }

    private void ping() {
        MainWindow.mainSock.call(ip, MainWindow.username, CallCodes.callPing);
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
    
    public void setSocket(Socket sock) {
        if (cc != null && cc.isAlive()) {
            cc.interrupt();
        }
        
        cc = new ConnectionChecker(sock, this);
        cc.start();
    }
    
    
    private class ConnectionChecker extends Thread {
        Socket sock;
        FriendPanel parent;
        
        public ConnectionChecker(Socket sock, FriendPanel parent) {
            this.sock = sock;
            this.parent = parent;
        }
        
        @Override
        public void run() {
            parent.refresh(Boolean.TRUE, Boolean.FALSE, username);
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                in.readLine();
            } catch (Exception ex) {
                // Logger.getLogger(FriendPanel.class.getName()).log(Level.SEVERE, null, ex);
                parent.refresh(Boolean.FALSE, Boolean.FALSE, username);
            } 
        }
    }
}
