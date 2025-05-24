package com.example.camera;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CameraFrame {
    private JFrame frame;
    private JLabel cameraScreen;
    private Timer timer;
    private CameraManager cameraManager;

    public static void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            CameraFrame cameraFrame = new CameraFrame();
            cameraFrame.initialize();
        });
    }

    private void initialize() {
        cameraManager = new CameraManager();

        // Настройка основного окна
        frame = new JFrame("Camera Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        // Панель для отображения камеры
        cameraScreen = new JLabel();
        cameraScreen.setHorizontalAlignment(JLabel.CENTER);
        frame.add(cameraScreen, BorderLayout.CENTER);

        // Добавление кнопок управления
        addControlPanel();

        frame.setVisible(true);
    }

    private void addControlPanel() {
        JPanel panel = new JPanel();

        JButton startBtn = new JButton("Start Camera");
        JButton stopBtn = new JButton("Stop Camera");
        JButton maskBtn = new JButton("Next Filter");

        startBtn.addActionListener(e -> startCamera());
        stopBtn.addActionListener(e -> stopCamera());
        maskBtn.addActionListener(e -> nextStyle());

        panel.add(startBtn);
        panel.add(stopBtn);
        panel.add(maskBtn);

        frame.add(panel, BorderLayout.SOUTH);
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
}