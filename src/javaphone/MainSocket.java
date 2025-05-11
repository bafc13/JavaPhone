/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;


import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Andrey
 */
public class MainSocket extends Thread {
    public static final int PORT = 666;
    ServerSocket main_sock;

    public MainSocket() throws IOException
    {
        main_sock = new ServerSocket(PORT);
    }

    @Override
    public void run()
    {
        try
        {
            while(true)
            {
                Socket sock = main_sock.accept();
            }
        }
        catch (IOException e)
        {

            try {
                main_sock.close();
            } catch (IOException ex) {
                Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
