/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone.EventInterfaces;

import javaphone.Handshake;

/**
 *
 * @author Andrey
 */
public interface CallHandler {
    void callRecieved(Handshake hs);
    void callSent(Handshake hs);
    void callFailed(String ip, String purpose);
}
