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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.swing.Timer;

/**
 *
 * @author bafc13
 */
public final class CallFrame extends javax.swing.JFrame {

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
    ChatArea chatArea;

    private boolean isCall = false;
    private int horizontalPanelSize;
    private int chatPanelSize;
    private Dimension screenSize;

    private int camerasCount = 0;
    /**
     * Creates new form CallFrame
     * @throws java.io.IOException
     */
    public CallFrame() throws IOException {

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
        chatPanelSize = screenSize.height - horizontalPanelSize;

        if(isCall == false){
            initChat();
        } else {
            initCall();
        }
        this.setVisible(true);
        this.setSize(screenSize.width, screenSize.height);
        initComponents();

    }

    public CallFrame(String ip, String nick) throws IOException {
        this();
        ipToConnect = ip;
        nickName = nick;
        System.out.println("IP AND NICK INITIALIZED");
    }

    public void setController(ApplicationController controller) {
        this.controller = controller;
    }

    private void initCall() throws IOException {
        this.setLayout(new BorderLayout());

        addCameraPanel();
        addChatUserPanel();


        OpenCVInitializer.init();
        subtitleDisplay = new SubtitleDisplay();
        addMyCamera();




        this.recognizer = new VoskSpeechRecognizer();
        controller = new ApplicationController(recognizer, subtitleDisplay);
        this.setController(controller);
        controller.start();
    }

    private void initChat() {
        addChatUserPanel();

        JButton callButton = new JButton();
        callButton.setText("Начать звонок");
        callButton.setSize(200,75);
        callButton.setMaximumSize(new Dimension(200,75));
        callButton.setPreferredSize(new Dimension(200,75));
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

    private void addCameraPanel(){
        //добавление панели для всех камер
        horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        horizontalPanel.setSize(screenSize.width, horizontalPanelSize);
        horizontalPanel.setMinimumSize(new Dimension(screenSize.width, horizontalPanelSize));
        horizontalPanel.setPreferredSize(new Dimension(screenSize.width, horizontalPanelSize));
        horizontalPanel.setMaximumSize(new Dimension(screenSize.width, horizontalPanelSize));
        this.add(horizontalPanel, BorderLayout.NORTH);
    }

    private void addChatUserPanel() {
        //
        ///
        ///
        ///
        //
        //тут надо размеры порасставлять
        ///
        ///
        ///
        ///
        //

        chatUserPanel = new JPanel();
        chatUserPanel.setLayout(new BoxLayout(chatUserPanel, BoxLayout.X_AXIS));
        chatUserPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        chatUserPanel.setMinimumSize(new Dimension(1920,312));
        chatUserPanel.setPreferredSize(new Dimension(1920,312));
        chatUserPanel.setMaximumSize(new Dimension(1920,312));
        chatUserPanel.setSize(screenSize.width, chatPanelSize);

        chatArea = new ChatArea(screenSize, isCall);
        chatUserPanel.add(chatArea, BorderLayout.SOUTH);

        this.add(chatUserPanel, BorderLayout.SOUTH);
        chatUserPanel.repaint();
        chatUserPanel.revalidate();

        this.repaint();
        this.revalidate();
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


        subtitleDisplay.getView().setMaximumSize(new Dimension(400,50));

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
            subtitleDisplay.getView().setMaximumSize(new Dimension(400,50));

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
            subtitleDisplay.getView().setMaximumSize(new Dimension(400,50));

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
        if (cameraManager.isCameraActive()) {
            startVideoStream();
        }

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

    private void startVideoStream() {
        timer = new Timer(30, e -> {
            BufferedImage image = cameraManager.getCurrentFrame();
            updateFrame(image);
        });
        timer.start();
    }

    private void stopVideoStream() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
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
        setMaximumSize(new java.awt.Dimension(1920, 1080));
        setMinimumSize(new java.awt.Dimension(1024, 768));
        setPreferredSize(new java.awt.Dimension(1920, 1080));
        setSize(new java.awt.Dimension(1920, 1080));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1140, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 573, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame jFrame1;
    // End of variables declaration//GEN-END:variables
}

