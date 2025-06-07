/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.example.OpenCVInitializer;
import com.example.camera.CameraManager;
import com.livesubtitles.core.ApplicationController;
import com.livesubtitles.speech.VoskSpeechRecognizer;
import com.livesubtitles.ui.SubtitleDisplay;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import javaphone.EventInterfaces.VideoHandler;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author bafc13
 */
public class CameraPanel extends javax.swing.JPanel implements VideoHandler {

    private final int chatID;

    private JLabel cameraScreen;
    public CameraManager cameraManager;
    private int camerasCount = 0;
    private int subtitleCount = 0;
    private int participantCount = 0;
    private Timer timer;


    private SubtitleDisplay subtitleDisplay;
    private VoskSpeechRecognizer recognizer;
    private ApplicationController controller;

    private JPanel horizontalPanel;
    private JPanel secondHorizontalPanel;

    private LinkedHashMap<Integer, JLabel> cameraMap;
    private LinkedHashMap<Integer, SubtitleDisplay> subtitleMap;
    private LinkedHashMap<String, Integer> participants;

    private VideoSender videoSender;
    private VideoReciever videoReciever;
    private Boolean videoEnabled;

    private CallFrame cf;

    private Dimension screenSize;


    public CameraPanel(JPanel horPanel, JPanel secHorPanel, LinkedHashMap<Integer, JLabel> cameraMap,
            LinkedHashMap<Integer, SubtitleDisplay> subtitleMap, int chatID,
            CallFrame cf) throws IOException {
        super();

        participants = new LinkedHashMap<>();
        participants.put("localhost", participantCount);
        participantCount++;

        this.cf = cf;
        this.horizontalPanel = horPanel;
        this.secondHorizontalPanel = secHorPanel;
        this.chatID = chatID;
        this.cameraMap = cameraMap;
        this.subtitleMap = subtitleMap;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

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

        OpenCVInitializer.init();
        subtitleDisplay = new SubtitleDisplay();
        subtitleMap.put(subtitleCount, subtitleDisplay);
        subtitleCount++;
        subtitleDisplay.getView().setMaximumSize(new Dimension(400, 50));
        southPanel.add(subtitleDisplay.getView());

        subtitleMap.put(subtitleCount, subtitleDisplay);
        subtitleCount++;

        addControlPanel(southPanel);

        myCameraPanel.add(southPanel, BorderLayout.SOUTH);
        this.add(myCameraPanel);

        cameraMap.put(camerasCount, cameraScreen);
        camerasCount++;

        this.recognizer = new VoskSpeechRecognizer();
        controller = new ApplicationController(recognizer, subtitleDisplay);
        this.setController(controller);
        controller.start();


//        this.videoSender = videoSender;
//        this.videoReciever = videoReciever;
//        this.videoReciever.addListener(this);
        videoEnabled = true;
    }

    public void addParticipant(String ip) {
        participants.put(ip, participantCount);
        participantCount++;

        addCamera();
    }

    public void addCamera() {
        JPanel CameraPanel = new JPanel();
        JLabel CameraScreen;
        if (cameraMap.size() < 2) {

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

            subtitleMap.put(subtitleCount, subtitleDisplay);
            subtitleCount++;

            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);
            horizontalPanel.add(CameraPanel);

            cameraMap.put(camerasCount, CameraScreen);
            camerasCount++;

            horizontalPanel.repaint();
            horizontalPanel.revalidate();
        } else if (cameraMap.size() < 4) {
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
            subtitleMap.put(subtitleCount, subtitleDisplay);
            subtitleCount++;

            southPanel.add(subtitleDisplay.getView());
            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);

            cameraMap.put(camerasCount, CameraScreen);
            camerasCount++;

            cameraMap.forEach((Integer key, JLabel camera) -> {
                int width = screenSize.width / cameraMap.size() - 100;
                int height = (int) (width * 0.66);
                camera.setMinimumSize(new Dimension(width, height));
                camera.setPreferredSize(new Dimension(width, height));
                camera.setMaximumSize(new Dimension(width, height));
                camera.setSize(new Dimension(width, height));
            });

            horizontalPanel.add(CameraPanel);

            horizontalPanel.repaint();
            horizontalPanel.revalidate();
        } else {
            if (cameraMap.size() == 4) {
                cf.addSecondCameraPanel();
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

            subtitleMap.put(subtitleCount, subtitleDisplay);
            subtitleCount++;

            addControlPanel(southPanel);

            CameraPanel.add(southPanel, BorderLayout.SOUTH);

            cameraMap.put(camerasCount, CameraScreen);
            camerasCount++;

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
        JButton addBtn = new JButton("Добавить");

        startBtn.addActionListener(e -> startCamera());
        startBtn.setBorder(new RoundedBorder(2));
        stopBtn.addActionListener(e -> stopCamera());
        stopBtn.setBorder(new RoundedBorder(2));
        maskBtn.addActionListener(e -> nextStyle());
        maskBtn.setBorder(new RoundedBorder(2));
        addBtn.addActionListener(e -> addCamera());

        panel.add(startBtn);
        panel.add(stopBtn);
        panel.add(maskBtn);
        panel.add(addBtn);

        panelToAdd.add(panel, BorderLayout.SOUTH);
    }

    public void setController(ApplicationController controller) {
        this.controller = controller;
    }

    public ApplicationController getController() {
        return controller;
    }


    @Override
    public void HandleCameraFrameRecieved(int chatID, String address, BufferedImage frame) {
        if (this.chatID != chatID)
            return;

        int cameraID = participants.get(address);
        BufferedImage img = resizeToCameraFrame(frame, cameraID);
        updateFrame(img, cameraID);
    }

    @Override
    public void HandleCameraFrameRecorded(BufferedImage frame) {
        BufferedImage img = resizeToCameraFrame(frame, 0);
        updateFrame(img, 0);
    }

    public void exitFromCall() {
        stopCamera();
        if(controller != null) {
            controller.stop();
        }
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

        private BufferedImage resizeToCameraFrame(BufferedImage frame, int cameraID)
    {
        int w = cameraMap.get(cameraID).getWidth();
        int h = cameraMap.get(cameraID).getHeight();
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

}
