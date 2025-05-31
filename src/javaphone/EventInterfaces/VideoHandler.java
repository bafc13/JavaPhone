/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone.EventInterfaces;

import java.awt.image.BufferedImage;

/**
 *
 * @author Andrey
 */
public interface VideoHandler {
    void HandleCameraFrameRecieved(int chatID, String address, BufferedImage frame);
    void HandleCameraFrameRecorded(BufferedImage frame);
}
