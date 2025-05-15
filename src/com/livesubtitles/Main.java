package com.livesubtitles;

import com.livesubtitles.core.ApplicationController;
import com.livesubtitles.speech.VoskSpeechRecognizer;
import com.livesubtitles.ui.MainWindow;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow mainWindow = new MainWindow();
                VoskSpeechRecognizer recognizer = new VoskSpeechRecognizer();
                ApplicationController controller = new ApplicationController(recognizer, mainWindow.getSubtitleDisplay());
                
                mainWindow.setController(controller);
                mainWindow.show();
                controller.start();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Ошибка инициализации: " + e.getMessage(), 
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}