/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

/**
 *
 * @author Andrey
 */
public class Handshake {
    public String message;
    public String address;
    public Integer port;
    
    public Handshake(String m, String a, Integer p)
    {
        message = m;
        address = a;
        port = p;
    }
}
