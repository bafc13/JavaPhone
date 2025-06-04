/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaphone;

import com.livesubtitles.audio.AudioConfig;
import com.livesubtitles.core.ApplicationController;
import javax.swing.*;
import java.awt.*;
import com.livesubtitles.ui.SubtitleDisplay;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.*;
import static javaphone.VideoSender.h;
import static javaphone.VideoSender.w;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.Timer;

/**
 *
 * @author bafc13
 */
public final class CallFrame extends javax.swing.JFrame implements VideoHandler, VoiceHandler, CallResultHandler, SubtitleHandler {

    private Dimension screenSize;
    private final int chatID;

    private AudioPlay audioPlay;

    private DirectMessenger dm;
    private VoiceSender voiceSender;
    private VoiceReciever voiceReceiver;
    private VideoSender videoSender;
    private VideoReciever videoReceiver;
    private WriterToFile log;

    private Boolean voiceEnabled;

    private String ipToConnect;
    private String nickName;

    private JPanel horizontalPanel;
    private JPanel secondHorizontalPanel = new JPanel();
    ;
    private JPanel chatUserPanel;
    private ChatArea chatArea;

    private boolean isCall = false;
    private int horizontalPanelSize;
    private int chatPanelSize;

    private Boolean videoEnabled;
    private CameraPanel cameraPanel;

    private ApplicationController controller;

    private LinkedHashMap<Integer, JLabel> cameraMap = new LinkedHashMap<>();
    private LinkedHashMap<Integer, SubtitleDisplay> subtitleMap = new LinkedHashMap<>();

    /**
     * Creates new form CallFrame
     *
     * @throws java.io.IOException
     */
    public CallFrame(DirectMessenger dm) throws IOException {
        chatID = dm.getID();
        this.dm = dm;

        dm.start();
        initCallFrame();

        // System.out.println("INITIALIZED WITH DM");
    }

    public CallFrame(DirectMessenger dm, VoiceSender voiceSender, VoiceReciever voiceReciever) throws IOException {
        chatID = dm.getID();
        this.dm = dm;

        initCallFrame();

        this.voiceSender = voiceSender;
        this.voiceReceiver = voiceReciever;

        this.voiceReceiver.addListener(this);

        voiceEnabled = true;

        // System.out.println("INITIALIZED WITH DM AND VOICE");
    }

    public CallFrame(DirectMessenger dm, VoiceSender voiceSender, VoiceReciever voiceReciever, VideoSender videoSender, VideoReciever videoReciever) throws IOException {
        chatID = dm.getID();
        this.dm = dm;
        initCallFrame();

        this.voiceSender = voiceSender;
        this.voiceReceiver = voiceReciever;

        this.videoSender = videoSender;
        this.videoReceiver = videoReciever;

        this.voiceReceiver.addListener(this);
        this.videoReceiver.addListener(this);
        voiceEnabled = true;
//        videoEnabled = true;

        // System.out.println("INITIALIZED WITH DM, VOICE AND VIDEO");
    }

    private void initCallFrame() throws IOException {
        mainJFrame.basicCallHandler.addListener(this);

        this.setTitle("Call");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        horizontalPanelSize = (screenSize.height / 2) + 100;
        chatPanelSize = screenSize.height / 3 - 50;

        if (isCall == false) {
            initChat();
        } else {
            initCall();
        }
        this.setVisible(true);
        this.setSize(screenSize.width, screenSize.height);
        initComponents();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        voiceEnabled = false;
        videoEnabled = false;
    }

    private void initCall() throws IOException {
        this.setLayout(new BorderLayout());

        addCameraPanel();
        addChatUserPanel();

        addMyCamera();

        String ip = dm.getIP();
        mainJFrame.mainSock.call(ip, mainJFrame.username, CallCodes.callVoice);
        mainJFrame.mainSock.call(ip, mainJFrame.username, CallCodes.callVideo);
    }

    private void initChat() {
        addChatUserPanel();

        JButton callButton = new JButton();
        callButton.setText("Начать звонок");
        callButton.setSize(200, 75);
        callButton.setMaximumSize(new Dimension(200, 75));
        callButton.setPreferredSize(new Dimension(200, 75));
        callButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        callButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        callButton.addActionListener((ActionEvent e) -> {
            isCall = true;
            chatUserPanel.removeAll();
            this.getContentPane().removeAll();
            CallFrame.this.repaint();
            CallFrame.this.revalidate();
            try {
                initCall();
            } catch (IOException ex) {
                Logger.getLogger(CallFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        this.add(callButton, BorderLayout.NORTH);
    }

    public ApplicationController getController() {
        return controller;
    }


    private void addCameraPanel() {
        //добавление панели для всех камер
        horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        horizontalPanel.setSize(screenSize.width, horizontalPanelSize);
        this.add(horizontalPanel, BorderLayout.NORTH);
    }

    public void addSecondCameraPanel() {
        System.out.println("\nYA GEI BLYAT\n");
        int width = screenSize.width / 4 - 100;
        int height = (int) (width * 0.66);
        cameraMap.get(0).setSize(width, height);

        horizontalPanel.setSize(screenSize.width, screenSize.height / 3);

        horizontalPanel.repaint();
        horizontalPanel.revalidate();
        this.repaint();
        this.revalidate();

        chatUserPanel.setSize(screenSize.width, screenSize.height / 3 - 50);
        chatUserPanel.repaint();
        chatUserPanel.revalidate();

        secondHorizontalPanel.setLayout(new BoxLayout(secondHorizontalPanel, BoxLayout.X_AXIS));
        secondHorizontalPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        secondHorizontalPanel.repaint();
        secondHorizontalPanel.revalidate();
        this.add(secondHorizontalPanel, BorderLayout.CENTER);

        this.repaint();
        this.revalidate();
    }

    private void addChatUserPanel() {

        chatUserPanel = new JPanel();
        chatUserPanel.setLayout(new BoxLayout(chatUserPanel, BoxLayout.X_AXIS));
        chatUserPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        if (isCall == false) {
            chatUserPanel.setMinimumSize(new Dimension(screenSize.width, chatPanelSize + 500));
            chatUserPanel.setPreferredSize(new Dimension(screenSize.width, chatPanelSize + 500));
            chatUserPanel.setMaximumSize(new Dimension(screenSize.width, chatPanelSize + 500));
            chatUserPanel.setSize(screenSize.width, chatPanelSize + 500);
        } else {
            chatUserPanel.setMinimumSize(new Dimension(screenSize.width, chatPanelSize));
            chatUserPanel.setPreferredSize(new Dimension(screenSize.width, chatPanelSize));
            chatUserPanel.setMaximumSize(new Dimension(screenSize.width, chatPanelSize));
            chatUserPanel.setSize(screenSize.width, chatPanelSize);
        }

        chatArea = new ChatArea(screenSize, isCall, dm);
        chatUserPanel.add(chatArea, BorderLayout.CENTER);

        this.add(chatUserPanel, BorderLayout.SOUTH);
        chatUserPanel.repaint();
        chatUserPanel.revalidate();
        this.repaint();
        this.revalidate();
    }

    @Override
    public void dispose() {
        if (cameraPanel != null) {
            cameraPanel.exitFromCall();
        }
        super.dispose();
    }

    private void addMyCamera() throws IOException {

        cameraPanel = new CameraPanel(horizontalPanel, secondHorizontalPanel, cameraMap, subtitleMap, chatID, this);
        horizontalPanel.add(cameraPanel);

        horizontalPanel.repaint();
        horizontalPanel.revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMaximumSize(new java.awt.Dimension(5200, 5200));
        setMinimumSize(new java.awt.Dimension(1024, 768));
        setPreferredSize(new java.awt.Dimension(5200, 5200));
        setSize(new java.awt.Dimension(1920, 1080));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1920, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1080, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame jFrame1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void HandleCameraFrameRecieved(int chatID, String address, BufferedImage frame) {

    }

    @Override
    public void HandleCameraFrameRecorded(BufferedImage frame) {

    }

    @Override
    public void HandleVoiceRecieved(int chatID, String address, byte[] audioChunk) {

        // System.out.println(String.valueOf(this.chatID) + " " + String.valueOf(chatID));
        if (this.chatID != chatID) {
            return;
        }

        audioPlay.playAudioForParticipant(dm.getIP(), audioChunk);
    }

    @Override
    public void HandleVoiceRecorded(byte[] audioChunk) {
        // DO NOTHING I GUESS
    }

    @Override
    public void DMCreated(int chatID, DirectMessenger dm) {

    }

    @Override
    public void VoiceCreated(int chatID, VoiceSender vs, VoiceReciever vr) {
        if (chatID != this.chatID) {
            return;
        }
        this.setLayout(new BorderLayout());


        addCameraPanel();
        addChatUserPanel();

        try {
            addMyCamera();
        } catch (IOException ex) {
            Logger.getLogger(CallFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.controller = cameraPanel.getController();

        audioPlay = new AudioPlay(AudioConfig.getAudioFormat());
        voiceSender = vs;
        voiceReceiver = vr;

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedNow = now.format(formatter);
        log = new WriterToFile(this.chatID, "./log/call" + formattedNow + ".txt");

        controller.getMic().addListener(voiceSender);
        controller.addListener(voiceSender);

        controller.addListener(this);

        voiceReceiver.addListener(this);
        voiceReceiver.addSubListener(this);

        voiceReceiver.addSubListener(log);
        controller.addListener(log);

        voiceReceiver.start();
        try {
            audioPlay.addParticipant("localhost");
            audioPlay.addParticipant(dm.getIP());
        } catch (LineUnavailableException ex) {
            Logger.getLogger(CallFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void VideoCreated(int chatID, VideoSender vs, VideoReciever vr) {
        if (chatID != this.chatID) {
            return;
        }
        videoSender = vs;
        videoReceiver = vr;

        cameraPanel.cameraManager.addListener(vs);
        vr.addListener(cameraPanel);
        cameraPanel.addParticipant(dm.getIP());
    }

    @Override
    public void SubtitleLineReceived(int chatID, String address, String line) {
        // System.out.println("Recieved: " + line);
    }

    @Override
    public void SubtitleLineRecorded(String line) {
        // System.out.println("Recorded: " + line);
    }

    @Override
    public void PingHappened(String address, String username) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
