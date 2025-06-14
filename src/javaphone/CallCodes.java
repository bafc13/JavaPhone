/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

/**
 *
 * @author Andrey
 */
public class CallCodes {
    public static final String callDM = "dm";
    public static final String callVoiceVideo = "voice";
    public static final String callPing = "ping";
    
    public static final String responseAccept = "ACCEPT";
    public static final String responseError = "ERROR";
    public static final String responseRefuze = "REFUZE";
    public static final String responseWait = "WAIT";
    
    public static final int dmText = 1;
    public static final int dmFile = 2;
    public static final int filePresent = 200;
    public static final int fileRequired = 404;
    
    public static final long delayOffline = 5000L;
    public static final long delayResponse = 60000L;
}
