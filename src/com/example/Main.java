package com.example;

import com.example.camera.CameraFrame;

public class Main {
    public static void main(String[] args) {
        // Инициализация OpenCV
        OpenCVInitializer.init();
        
        // Создание и отображение GUI
        CameraFrame.createAndShowGUI();
    }
}