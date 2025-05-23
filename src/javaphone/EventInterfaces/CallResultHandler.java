/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone.EventInterfaces;

import javaphone.DirectMessenger;
import javaphone.VoiceReciever;
import javaphone.VoiceSender;

/**
 *
 * @author Andrey
 */
public interface CallResultHandler {
    void DMCreated(DirectMessenger dm);
    void VoiceCreated(VoiceSender vs, VoiceReciever vr);
}
