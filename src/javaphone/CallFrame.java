/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package javaphone;

import com.example.OpenCVInitializer;
import com.example.camera.CameraManager;
import com.livesubtitles.core.ApplicationController;
import com.livesubtitles.speech.VoskSpeechRecognizer;
import com.livesubtitles.ui.SubtitleDisplay;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.Timer;

/**
 *
 * @author bafc13
 */
public final class CallFrame extends javax.swing.JFrame {

//    private SpeechRecognizer speechRec;
    private ApplicationController controller;
    private SubtitleDisplay subtitleDisplay;
    private VoskSpeechRecognizer recognizer;

    private List<JPanel> cameras;

    private JLabel cameraScreen;
    private Timer timer;
    private CameraManager cameraManager;
    JPanel horizontalPanel;


    /**
     * Creates new form CallFrame
     * @throws java.io.IOException
     */
    public CallFrame() throws IOException {
        this.setTitle("Call");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.width);
//        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());



        //добавление панели для всех камер
        horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        this.add(horizontalPanel, BorderLayout.CENTER);


        //панель для своей камеры
        JPanel myCameraPanel = new JPanel();
        myCameraPanel.setLayout(new BoxLayout(myCameraPanel, BoxLayout.Y_AXIS));

        JPanel southPanel = new JPanel(); //панель для кнопок
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));


        addControlPanel(southPanel);

        OpenCVInitializer.init();
        cameraManager = new CameraManager();

        cameraScreen = new JLabel();
        cameraScreen.setHorizontalAlignment(JLabel.CENTER);
        myCameraPanel.add(cameraScreen);


        subtitleDisplay = new SubtitleDisplay();
        southPanel.add(subtitleDisplay.getView());
//        myCameraPanel.add(southPanel);
//        addControlPanel(myCameraPanel);
//        addControlPanel(southPanel);

        this.recognizer = new VoskSpeechRecognizer();
        controller = new ApplicationController(recognizer, subtitleDisplay);
        this.setController(controller);

        myCameraPanel.add(southPanel, BorderLayout.SOUTH);
        horizontalPanel.add(myCameraPanel);

        controller.start();
        this.setVisible(true);

        initComponents();
    }

    public void setController(ApplicationController controller) {
        this.controller = controller;
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                controller.stop();
            }
        });
    }

    private void addCamera(){
        JPanel myCameraPanel = new JPanel();
        myCameraPanel.setLayout(new BoxLayout(myCameraPanel, BoxLayout.Y_AXIS));

        OpenCVInitializer.init();
        cameraManager = new CameraManager();

        cameraScreen = new JLabel();
        cameraScreen.setHorizontalAlignment(JLabel.CENTER);
        myCameraPanel.add(cameraScreen, BorderLayout.CENTER);


        subtitleDisplay = new SubtitleDisplay();
        myCameraPanel.add(subtitleDisplay.getView());
//        myCameraPanel.add(southPanel);
        addControlPanel(myCameraPanel);

        horizontalPanel.add(myCameraPanel);

    }

    private void addControlPanel(JPanel panelToAdd) {
        JPanel panel = new JPanel();

        JButton startBtn = new JButton("Вкл камеру");
        JButton stopBtn = new JButton("Выкл камеру");
        JButton maskBtn = new JButton("Фильтр");
        JButton exitBtn = new JButton("Выход");

        startBtn.addActionListener(e -> startCamera());
        stopBtn.addActionListener(e -> stopCamera());
        maskBtn.addActionListener(e -> nextStyle());
        exitBtn.addActionListener(e -> exitFromCall());


        panel.add(startBtn);
        panel.add(stopBtn);
        panel.add(maskBtn);
        panel.add(exitBtn);

        panelToAdd.add(panel);
    }

    private void exitFromCall() {
        stopCamera();
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
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
        setPreferredSize(new java.awt.Dimension(1024, 768));
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

