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
public class DirectMessenger extends Thread {
    //Andrey lox
    
    Boolean is_host;
    Socket source;
    BufferedReader in;
    BufferedWriter out;
    
    public DirectMessenger(Boolean is_host, Socket s) throws IOException
    {
        this.is_host = is_host;
        source = s;
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }
    
    @Override
    public void run() 
    {
        String msg;        
        try 
        {
            while(true)
            {
                msg = in.readLine();
                
                // Call event handle_dm(is_sender=false, source, word)
            }
        } 
        catch (IOException ex) 
        {
                Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String msg) throws IOException
    {
        out.write(msg);
        out.flush();
        
        // Call event handle_dm(is_sender=true, source, word)
    }
}
