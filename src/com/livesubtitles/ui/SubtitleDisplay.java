package com.livesubtitles.ui;

import javax.swing.*;
import java.awt.*;

public class SubtitleDisplay {
    private final JPanel panel;
    private final JLabel subtitleLabel;
    private final Color activeColor = new Color(0, 100, 0, 255);
    private final Color inactiveColor = new Color(0, 0, 0, 255);
    private final Color finalResultColor = new Color(0, 0, 100, 255);

    public SubtitleDisplay() {
        panel = new JPanel(new BorderLayout());
        panel.setBackground(inactiveColor);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        subtitleLabel = new JLabel("", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial Unicode MS", Font.BOLD, 36));
        subtitleLabel.setForeground(Color.WHITE);

        panel.add(subtitleLabel, BorderLayout.NORTH);
    }

    public JPanel getView() {
        return panel;
    }

    public void updateText(String text, boolean isFinal) {
        if (text == null || text.isEmpty()) {
            subtitleLabel.setText("");
            panel.setBackground(inactiveColor);
            return;
        }

        subtitleLabel.setText(text);
        if (isFinal) {
            panel.setBackground(finalResultColor);
            subtitleLabel.setForeground(Color.WHITE);
        } else {
            panel.setBackground(activeColor);
            subtitleLabel.setForeground(Color.YELLOW);
        }
    }
}