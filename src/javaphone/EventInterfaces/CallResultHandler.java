/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone.EventInterfaces;

import javaphone.DirectMessenger;
import javaphone.VideoReciever;
import javaphone.VideoSender;
import javaphone.VoiceReciever;
import javaphone.VoiceSender;

/**
 *
 * @author Andrey
 */
public interface CallResultHandler {
    void DMCreated(int chatID, DirectMessenger dm);
    void VoiceCreated(int chatID, VoiceSender vs, VoiceReciever vr);
    void VideoCreated(int chatID, VideoSender vs, VideoReciever vr);
    void PingHappened(String address, String username);
}
