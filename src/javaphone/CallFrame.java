/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaphone;

import javax.swing.*;
import java.awt.*;
import com.example.OpenCVInitializer;
import com.example.camera.CameraManager;
import com.livesubtitles.audio.AudioConfig;
import com.livesubtitles.core.ApplicationController;
import com.livesubtitles.speech.VoskSpeechRecognizer;
import com.livesubtitles.ui.SubtitleDisplay;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private JPanel secondHorizontalPanel;
    private JPanel chatUserPanel;
    ChatArea chatArea;

    private boolean isCall = false;
    private int horizontalPanelSize;
    private int chatPanelSize;

    private int camerasCount = 0;

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

        System.out.println("INITIALIZED WITH DM");
    }

    public CallFrame(DirectMessenger dm, VoiceSender voiceSender, VoiceReciever voiceReciever) throws IOException {
        chatID = dm.getID();
        this.dm = dm;
        
        initCallFrame();
        
        this.voiceSender = voiceSender;
        this.voiceReceiver = voiceReciever;

        this.voiceReceiver.addListener(this);

        voiceEnabled = true;

        System.out.println("INITIALIZED WITH DM AND VOICE");
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
        videoEnabled = true;

        System.out.println("INITIALIZED WITH DM, VOICE AND VIDEO");
    }

    private void initCallFrame() throws IOException {
        cameras = new Vector<>();
        
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

        voiceEnabled = false;
        videoEnabled = false;
        mainJFrame.basicCallHandler.addListener(this);
    }

    public void setController(ApplicationController controller) {
        this.controller = controller;
    }

    private void initCall() throws IOException {
        // Is it supposed to be here?? (must happen when call button is pressed)
        String ip = dm.getIP().substring(1);
        mainJFrame.mainSock.call(ip, mainJFrame.username, CallCodes.voiceCall);
        System.out.println("call 1 done");
        mainJFrame.mainSock.call(ip, mainJFrame.username, CallCodes.videoCall);
        System.out.println("call 2 done");
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

    private void addCameraPanel() {
        //добавление панели для всех камер
        horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        horizontalPanel.setSize(screenSize.width, horizontalPanelSize);
        this.add(horizontalPanel, BorderLayout.NORTH);
    }

    private void addSecondCameraPanel() {
        horizontalPanel.setSize(screenSize.width, screenSize.height / 3);

        horizontalPanel.repaint();
        horizontalPanel.revalidate();

        chatUserPanel.setSize(screenSize.width, screenSize.height / 3 - 50);
        chatUserPanel.repaint();
        chatUserPanel.revalidate();

        secondHorizontalPanel = new JPanel();
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

    private void addMyCamera() {
        //панель для своей камеры
        JPanel myCameraPanel = new JPanel();
        myCameraPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        myCameraPanel.setLayout(new BoxLayout(myCameraPanel, BoxLayout.Y_AXIS));
        myCameraPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel southPanel = new JPanel(); //панель для кнопок
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cameraManager = new CameraManager();

        cameraManager.addListener(this);

        cameraScreen = new JLabel("Zzzzz...");
        cameraScreen.setAlignmentX(Component.CENTER_ALIGNMENT);

        cameraScreen.setBorder(new RoundedBorder(3));
        cameraScreen.setMinimumSize(new Dimension(600, 400));
        cameraScreen.setPreferredSize(new Dimension(600, 400));
        cameraScreen.setMaximumSize(new Dimension(600, 400));
        myCameraPanel.add(cameraScreen);

        subtitleDisplay.getView().setMaximumSize(new Dimension(400, 50));

        southPanel.add(subtitleDisplay.getView());
        addControlPanel(southPanel);

        myCameraPanel.add(southPanel, BorderLayout.SOUTH);
        horizontalPanel.add(myCameraPanel);

        horizontalPanel.repaint();
        horizontalPanel.revalidate();
        camerasCount++;
        cameras.add(cameraScreen);
    }

    private void addCamera() {
        JPanel CameraPanel = new JPanel();
        JLabel CameraScreen;
        if (cameras.size() < 2) {

            CameraPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
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
            subtitleDisplay.getView().setMaximumSize(new Dimension(400, 50));

            southPanel.add(subtitleDisplay.getView());
            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);
            camerasCount++;
            cameras.add(CameraScreen);

            horizontalPanel.add(CameraPanel);

            horizontalPanel.repaint();
            horizontalPanel.revalidate();
        } else if (cameras.size() < 4) {
            CameraPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            CameraPanel.setLayout(new BoxLayout(CameraPanel, BoxLayout.Y_AXIS));

            JPanel southPanel = new JPanel();
            southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

            CameraScreen = new JLabel("Zzzzz...");
            CameraScreen.setAlignmentX(Component.CENTER_ALIGNMENT);
            CameraScreen.setBorder(new RoundedBorder(3));

            CameraPanel.add(CameraScreen);

            subtitleDisplay = new SubtitleDisplay();
            subtitleDisplay.getView().setMaximumSize(new Dimension(400, 50));

            southPanel.add(subtitleDisplay.getView());
            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);
            camerasCount++;
            cameras.add(CameraScreen);

            for (JLabel camera : cameras) {
                int width = screenSize.width / cameras.size() - 100;
                int height = (int) (width * 0.66);
                camera.setMinimumSize(new Dimension(width, height));
                camera.setPreferredSize(new Dimension(width, height));
                camera.setMaximumSize(new Dimension(width, height));
                horizontalPanel.repaint();
                horizontalPanel.revalidate();
            }
            horizontalPanel.add(CameraPanel);

            horizontalPanel.repaint();
            horizontalPanel.revalidate();
        } else {
            if (cameras.size() == 4) {
                addSecondCameraPanel();
            }

            CameraPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
            CameraPanel.setLayout(new BoxLayout(CameraPanel, BoxLayout.Y_AXIS));

            JPanel southPanel = new JPanel();
            southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

            CameraScreen = new JLabel("Zzzzz...");
            CameraScreen.setAlignmentX(Component.CENTER_ALIGNMENT);
            CameraScreen.setBorder(new RoundedBorder(3));

            CameraPanel.add(CameraScreen);

            subtitleDisplay = new SubtitleDisplay();
            subtitleDisplay.getView().setMaximumSize(new Dimension(400, 50));

            southPanel.add(subtitleDisplay.getView());
            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);
            camerasCount++;
            cameras.add(CameraScreen);

            int width = screenSize.width / 4 - 100;
            int height = (int) (width * 0.66);

            CameraScreen.setSize(width, height);
            CameraScreen.setPreferredSize(new Dimension(width, height));
            CameraScreen.setMaximumSize(new Dimension(width, height));
            CameraScreen.setMinimumSize(new Dimension(width, height));

            secondHorizontalPanel.add(CameraPanel);

            secondHorizontalPanel.repaint();
            secondHorizontalPanel.revalidate();
        }
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

    private void nextStyle() {
        CameraManager.StyleCount = (CameraManager.StyleCount + 1) % 6;
    }

    private void stopCamera() {
        cameraManager.stopCamera();
        stopVideoStream();
        updateFrame(null, 0); // Очистка экрана
    }

    private void updateFrame(BufferedImage image, int cameraID) {
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
    
    private BufferedImage resizeToCameraFrame(BufferedImage frame, int cameraID)
    {
        int w = cameras.get(cameraID).getWidth();
        int h = cameras.get(cameraID).getHeight();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = frame.getWidth();
        int hh = frame.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++) {
            ys[y] = y * hh / h;
        }
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = frame.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        
        return img;
    }
    
    @Override
    public void HandleCameraFrameRecieved(int chatID, String address, BufferedImage frame) {
        if (this.chatID != chatID)
            return;
        
        int cameraID = -1; // TODO: find camera that handles sander
        BufferedImage img = resizeToCameraFrame(frame, cameraID);
        updateFrame(img, cameraID);
    }

    @Override
    public void HandleCameraFrameRecorded(BufferedImage frame) {
        BufferedImage img = resizeToCameraFrame(frame, 0);
        updateFrame(img, 0);
    }

    @Override
    public void HandleVoiceRecieved(int chatID, String address, byte[] audioChunk) {
        System.out.println(String.valueOf(this.chatID) + " " + String.valueOf(chatID));
        if (this.chatID != chatID)
            return;
        
        audioPlay.playAudioForParticipant(dm.getIP(), audioChunk);
    }

    @Override
    public void HandleVoiceRecorded(byte[] audioChunk) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void DMCreated(int chatID, DirectMessenger dm) {
        
    }

    @Override
    public void VoiceCreated(int chatID, VoiceSender vs, VoiceReciever vr) {
        if (chatID != this.chatID)
            return;
        
        this.setLayout(new BorderLayout()); 

        addCameraPanel();
        addChatUserPanel();

        OpenCVInitializer.init();
        subtitleDisplay = new SubtitleDisplay();
        addMyCamera();

        try {
            this.recognizer = new VoskSpeechRecognizer();
        } catch (IOException ex) {
            Logger.getLogger(CallFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller = new ApplicationController(recognizer, subtitleDisplay);
        this.setController(controller);
        controller.start();
        
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
        
        try {
            audioPlay.addParticipant("localhost");
            audioPlay.addParticipant(dm.getIP());
        } catch (LineUnavailableException ex) {
            Logger.getLogger(CallFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void VideoCreated(int chatID, VideoSender vs, VideoReciever vr) {
        if (chatID != this.chatID)
            return;
        
        // Do stuff
    }

    @Override
    public void SubtitleLineReceived(int chatID, String address, String line) {
        System.out.println("Recieved: " + line);
    }

    @Override
    public void SubtitleLineRecorded(String line) {
        System.out.println("Recorded: " + line);
    }
}
