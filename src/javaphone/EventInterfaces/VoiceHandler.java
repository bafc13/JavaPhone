/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone.EventInterfaces;

/**
 *
 * @author Andrey
 */
public interface VoiceHandler {
    void HandleVoiceRecieved(int chatID, String address, byte[] audioChunk);
    void HandleVoiceRecorded(byte[] audioChunk);
}
