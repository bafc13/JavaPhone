/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.image.BufferedImage;

/**
 *
 * @author Andrey
 */
public interface JavaPhoneEvents {
    void callRecieved(Handshake hs);
    void callSent(Handshake hs);
    
    void handleDM_text(String dm_address, String address, String text);
    void handleDM_file(String dm_address, String address, String fname);
    
    void handleVoiceRecieved(String dm_address, String address, byte[] audioChunk);
    void handleVoiceSent(String dm_address, String address, byte[] audioChunk);
    
    void handleCameraFrameRecieved(String dm_address, String address, BufferedImage frame);
    void handleCameraFrameSent(String dm_address, String address, BufferedImage frame);
}
