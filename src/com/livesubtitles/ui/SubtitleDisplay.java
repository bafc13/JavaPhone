package com.livesubtitles.ui;

import javax.swing.*;
import java.awt.*;

public class SubtitleDisplay {
    private final JPanel panel;
    private final JTextArea subtitleArea;
    private final Color activeColor = new Color(0, 100, 0, 255);
    private final Color inactiveColor = new Color(0, 0, 0, 255);
    private final Color finalResultColor = new Color(0, 0, 100, 255);

    public SubtitleDisplay() {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(inactiveColor);

        subtitleArea = new JTextArea();
        subtitleArea.setFont(new Font("Arial Unicode MS", Font.BOLD, 22));
        subtitleArea.setEditable(false);
        subtitleArea.setLineWrap(true);

        panel.add(subtitleArea, BorderLayout.CENTER);
    }

    public JPanel getView() {
        return panel;
    }

    public void updateText(String text, boolean isFinal) {
        if (text == null || text.isEmpty()) {
            subtitleArea.setText("");
            panel.setBackground(inactiveColor);
            return;
        }

        subtitleArea.setText(text);
        if (isFinal) {
            panel.setBackground(finalResultColor);
        } else {
            panel.setBackground(activeColor);
        }
    }
}