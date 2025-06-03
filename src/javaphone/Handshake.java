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

    public final DatagramSocket dSockReceive;
    public final DatagramSocket dSockSend;
    public final int port;
    public final int packetSize;

    public Handshake(String n, String m, Socket s) {
        name = n;
        message = m;
        sock = s;
        
        dSockReceive = null;
        dSockSend = null;
        port = -1;
        packetSize = -1;
    }

    public Handshake(String n, String m, Socket s, DatagramSocket dsr, DatagramSocket dss, int p, int ps) {
        name = n;
        message = m;
        sock = s;
        
        dSockReceive = dsr;
        dSockSend = dss;
        port = p;
        packetSize = ps;
    }
}
