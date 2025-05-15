package com.example;

import org.opencv.core.Core;
import nu.pattern.OpenCV;

public class OpenCVInitializer {
    public static void init() {
        OpenCV.loadLocally(); 
        System.out.println("OpenCV loaded successfully. Version: " + Core.VERSION);
    }
}