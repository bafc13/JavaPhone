/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.net.*;

/**
 *
 * @author Andrey
 */
public class Handshake {

    public final String name;
    public final String message;
    public final Socket sock;

    public final DatagramSocket dSockRecVoice;
    public final DatagramSocket dSockSndVoice;
    public final DatagramSocket dSockRecVideo;
    public final DatagramSocket dSockSndVideo;
    public final int voicePort;
    public final int videoPort;
    
    public final int voiceChunkSize;
    public final int videoChunkSize;

    public Handshake(String n, String m, Socket s) {
        name = n;
        message = m;
        sock = s;
        
        dSockRecVoice = null;
        dSockSndVoice = null;
        dSockRecVideo = null;
        dSockSndVideo = null;
        
        voicePort = -1;
        videoPort = -1;
        
        voiceChunkSize = -1;
        videoChunkSize = -1;
    }

    public Handshake(String n, String m, Socket s, DatagramSocket dsrVo, DatagramSocket dssVo, DatagramSocket dsrVi, DatagramSocket dssVi, int portVo, int portVi, int psVo, int psVi) {
        name = n;
        message = m;
        sock = s;
        
        dSockRecVoice = dsrVo;
        dSockSndVoice = dssVo;
        dSockRecVideo = dsrVi;
        dSockSndVideo = dsrVi;
        
        voicePort = portVo;
        videoPort = portVi;
        
        voiceChunkSize = psVo;
        videoChunkSize = psVi;
    }
}
