/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaphone;

import javax.swing.*;
import java.awt.*;
import com.example.OpenCVInitializer;
import com.example.camera.CameraManager;
import com.livesubtitles.core.ApplicationController;
import com.livesubtitles.speech.VoskSpeechRecognizer;
import com.livesubtitles.ui.SubtitleDisplay;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.*;


import javax.swing.Timer;

/**
 *
 * @author bafc13
 */
public final class CallFrame extends javax.swing.JFrame implements DMHandler, VideoHandler, VoiceHandler {
    private Dimension screenSize;
    
    private DirectMessenger dm;
    private VoiceSender voiceSender;
    private VoiceReciever voiceReciever;
    private VideoSender videoSender;
    private VideoReciever videoReciever;
    
    private Boolean voiceEnabled;
    private Boolean videoEnabled;
    
    private String ipToConnect;
    private String nickName;

    private ApplicationController controller;
    private SubtitleDisplay subtitleDisplay;
    private VoskSpeechRecognizer recognizer;

    private Vector<JLabel> cameras;

    private JLabel cameraScreen;
    private Timer timer;
    private CameraManager cameraManager;
    private JPanel horizontalPanel;
    private JPanel chatUserPanel;
    private JScrollPane chatPane;
    private JScrollPane userPane;
    private JTextArea chatArea;
    private JPanel chatPanel;
    private JTextArea userArea;
    private JTextField inputField;

    private int camerasCount = 0;
    /**
     * Creates new form CallFrame
     * @throws java.io.IOException
     */
    public CallFrame() throws IOException {
        cameras = new Vector<>();

        this.setTitle("Call");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        this.setResizable(true);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());


        //добавление панели для всех камер
        horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        horizontalPanel.setMinimumSize(new Dimension(screenSize.width, 550));
        horizontalPanel.setPreferredSize(new Dimension(screenSize.width, 550));
        horizontalPanel.setMaximumSize(new Dimension(screenSize.width, 550));
        this.add(horizontalPanel, BorderLayout.NORTH);

        addChatUserPanel();

        OpenCVInitializer.init();
        subtitleDisplay = new SubtitleDisplay();
        addMyCamera();

        this.recognizer = new VoskSpeechRecognizer();
        controller = new ApplicationController(recognizer, subtitleDisplay);
        this.setController(controller);

        controller.start();
        this.setVisible(true);

        initComponents();
        this.setSize(screenSize.width, screenSize.height);
        
        voiceEnabled = false;
        videoEnabled = false;
    }

    public CallFrame(DirectMessenger dm) throws IOException {
        this();
        this.dm = dm;
        
        System.out.println("INITIALIZED WITH DM");
    }
    
    public CallFrame(DirectMessenger dm, VoiceSender voiceSender, VoiceReciever voiceReciever) throws IOException {
        this();
        this.dm = dm;
        this.voiceSender = voiceSender;
        this.voiceReciever = voiceReciever;
        
        this.voiceReciever.addListener(this);
        
        voiceEnabled = true;
        
        System.out.println("INITIALIZED WITH DM AND VOICE");
    }
    
    public CallFrame(DirectMessenger dm, VoiceSender voiceSender, VoiceReciever voiceReciever, VideoSender videoSender, VideoReciever videoReciever) throws IOException {
        this();
        this.dm = dm;
        this.voiceSender = voiceSender;
        this.voiceReciever = voiceReciever;
        this.videoSender = videoSender;
        this.videoReciever = videoReciever;
        
        this.voiceReciever.addListener(this);
        this.videoReciever.addListener(this);
        
        voiceEnabled = true;
        videoEnabled = true;
        
        System.out.println("INITIALIZED WITH DM, VOICE AND VIDEO");
    }

    public void setController(ApplicationController controller) {
        this.controller = controller;

    }

    private void addChatUserPanel() {
        chatUserPanel = new JPanel();
        chatUserPanel.setLayout(new BoxLayout(chatUserPanel, BoxLayout.X_AXIS));
        chatUserPanel.setBorder(BorderFactory.createEmptyBorder(0, 350, 0, 5));
        chatUserPanel.setMinimumSize(new Dimension(screenSize.width, 312));
        chatUserPanel.setPreferredSize(new Dimension(screenSize.width, 312));
        chatUserPanel.setMaximumSize(new Dimension(screenSize.width, 312));

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
        inputField.addActionListener(e -> sendMessageText());

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
        userArea.append("kuz`ma\n");
        userArea.append("danilka!\n");
        userArea.append("alprexxxxxxx\n");
        userArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPane.setBorder(new RoundedBorder(5));

        chatUserPanel.add(chatPanel, BorderLayout.CENTER);
        chatUserPanel.add(Box.createHorizontalStrut(10));
        chatUserPanel.add(userPane, BorderLayout.CENTER);

        this.add(chatUserPanel, BorderLayout.SOUTH);
        chatUserPanel.repaint();
        chatUserPanel.revalidate();
    }

    private void sendMessageText()
    {
        try {
            dm.sendText(inputField.getText());
            inputField.setText("");
        } catch (IOException ex) {
            Logger.getLogger(CallFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addMyCamera() {
        //панель для своей камеры
        JPanel myCameraPanel = new JPanel();
        myCameraPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        myCameraPanel.setLayout(new BoxLayout(myCameraPanel, BoxLayout.Y_AXIS));
        myCameraPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel southPanel = new JPanel(); //панель для кнопок
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cameraManager = new CameraManager();

        cameraScreen = new JLabel("Zzzzz...");
        cameraScreen.setAlignmentX(Component.CENTER_ALIGNMENT);

        cameraScreen.setBorder(new RoundedBorder(3));
        cameraScreen.setMinimumSize(new Dimension(600, 400));
        cameraScreen.setPreferredSize(new Dimension(600, 400));
        cameraScreen.setMaximumSize(new Dimension(600, 400));
        myCameraPanel.add(cameraScreen);


        subtitleDisplay.getView().setMaximumSize(new Dimension(550,50));

        southPanel.add(subtitleDisplay.getView());
        addControlPanel(southPanel);

        myCameraPanel.add(southPanel, BorderLayout.SOUTH);
        horizontalPanel.add(myCameraPanel);

        horizontalPanel.repaint();
        horizontalPanel.revalidate();
        camerasCount++;
        cameras.add(cameraScreen);
    }

    private void addCamera(){
        JPanel CameraPanel = new JPanel();
        JLabel CameraScreen;
        if(cameras.size() < 2) {

            CameraPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            CameraPanel.setLayout(new BoxLayout(CameraPanel, BoxLayout.Y_AXIS));

            JPanel southPanel = new JPanel();
            southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));


            CameraScreen = new JLabel("Zzzzz...");
            CameraScreen.setAlignmentX(Component.CENTER_ALIGNMENT);
            CameraScreen.setBorder(new RoundedBorder(3));
            CameraScreen.setMinimumSize(new Dimension(600, 400));
            CameraScreen.setPreferredSize(new Dimension(600, 400));
            CameraScreen.setMaximumSize(new Dimension(600, 400));
            CameraPanel.add(CameraScreen);


            subtitleDisplay = new SubtitleDisplay();
            subtitleDisplay.getView().setMaximumSize(new Dimension(550,50));

            southPanel.add(subtitleDisplay.getView());
            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);
            camerasCount++;
            cameras.add(CameraScreen);
        } else {
            CameraPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            CameraPanel.setLayout(new BoxLayout(CameraPanel, BoxLayout.Y_AXIS));

            JPanel southPanel = new JPanel();
            southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));


            CameraScreen = new JLabel("Zzzzz...");
            CameraScreen.setAlignmentX(Component.CENTER_ALIGNMENT);
            CameraScreen.setBorder(new RoundedBorder(3));

            CameraPanel.add(CameraScreen);


            subtitleDisplay = new SubtitleDisplay();
            subtitleDisplay.getView().setMaximumSize(new Dimension(550,50));

            southPanel.add(subtitleDisplay.getView());
            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);
            camerasCount++;
            cameras.add(CameraScreen);

            for(JLabel camera : cameras){
                int width = 1920 / cameras.size() - 100;
                int height = width - 125;
                camera.setMinimumSize(new Dimension(width, height));
                camera.setPreferredSize(new Dimension(width, height));
                camera.setMaximumSize(new Dimension(width, height));
                horizontalPanel.repaint();
                horizontalPanel.revalidate();
            }
        }

        horizontalPanel.add(CameraPanel);

        horizontalPanel.repaint();
        horizontalPanel.revalidate();


    }

    private void addControlPanel(JPanel panelToAdd) {
        JPanel panel = new JPanel();

        JButton startBtn = new JButton("Вкл камеру");
        JButton stopBtn = new JButton("Выкл камеру");
        JButton maskBtn = new JButton("Фильтр");
        JButton exitBtn = new JButton("Выход");
        JButton addBtn = new JButton("Добавить");

        startBtn.addActionListener(e -> startCamera());
        startBtn.setBorder(new RoundedBorder(2));
        stopBtn.addActionListener(e -> stopCamera());
        stopBtn.setBorder(new RoundedBorder(2));
        maskBtn.addActionListener(e -> nextStyle());
        maskBtn.setBorder(new RoundedBorder(2));
        exitBtn.addActionListener(e -> exitFromCall());
        exitBtn.setBorder(new RoundedBorder(2));
        addBtn.addActionListener(e -> addCamera());
        addBtn.setBorder(new RoundedBorder(2));

        panel.add(startBtn);
        panel.add(stopBtn);
        panel.add(maskBtn);
        panel.add(exitBtn);
        panel.add(addBtn);

        panelToAdd.add(panel, BorderLayout.SOUTH);

        horizontalPanel.repaint();
        horizontalPanel.revalidate();
    }

    private void exitFromCall() {
        stopCamera();
        this.dispose();
     }

    @Override
    public void dispose() {
        controller.stop();
        super.dispose();
    }

    private void startCamera() {
        cameraManager.startCamera();
    }
    private void nextStyle(){
        CameraManager.StyleCount = (CameraManager.StyleCount+1)%6 ;
    }

    private void stopCamera() {
        cameraManager.stopCamera();
        stopVideoStream();
        updateFrame(null); // Очистка экрана
    }

    private void updateFrame(BufferedImage image) {
        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            cameraScreen.setIcon(icon);
        } else {
            cameraScreen.setIcon(null);
            cameraScreen.setText("Zzzzz...");
        }
    }

    private void stopVideoStream() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            videoSender.HandleCameraFrameRecorded(null);
        }
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
    public void HandleDMText(String dm_address, String address, String text) {
        String username = mainJFrame.db.getUsername(address);
        chatArea.append(username + ": " + text + "\n");
    }

    @Override
    public void HandleDMFile(String dm_address, String address, String fname) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void HandleCameraFrameRecieved(String dm_address, String address, BufferedImage frame) {
        if (frame != null) {
            ImageIcon icon = new ImageIcon(frame);
            cameras.get(1).setIcon(icon);
        } else {
            cameras.get(1).setIcon(null);
            cameras.get(1).setText("Zzzzz...");
        }
    }

    @Override
    public void HandleCameraFrameRecorded(BufferedImage frame) {
        updateFrame(frame);
    }

    @Override
    public void HandleVoiceRecieved(String dm_address, String address, byte[] audioChunk) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void HandleVoiceRecorded(byte[] audioChunk) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

