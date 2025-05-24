package com.livesubtitles.ui;


import com.livesubtitles.core.ApplicationController;
import javax.swing.*;
import java.awt.*;

public class MainWindow {
    private final JFrame frame;
    private final SubtitleDisplay subtitleDisplay;
    private ApplicationController controller;


    public MainWindow() {
        frame = new JFrame("Live Subtitles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        subtitleDisplay = new SubtitleDisplay();
        frame.add(subtitleDisplay.getView(), BorderLayout.CENTER);
    }

    public void setController(ApplicationController controller) {
        this.controller = controller;
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                controller.stop();
            }
        });
    }

    public SubtitleDisplay getSubtitleDisplay() {
        return subtitleDisplay;
    }

    public void show() {
        frame.setVisible(true);
    }
}