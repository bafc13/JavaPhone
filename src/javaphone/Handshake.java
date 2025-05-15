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
    public String name;
    public String message;
    public Socket sock;
    
    public Handshake(String n, String m, Socket s)
    {
        name = n;
        message = m;
        sock = s;
    }
}
