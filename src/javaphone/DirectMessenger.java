/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Andrey
 */
public class DirectMessenger extends Thread {
    //Andrey lox
       
    private final Boolean is_host;
    private final Socket source;
    private final BufferedReader in;
    private final BufferedWriter out;
    public static String type_text = "text\n";
    public static String type_image = "image\n";
    public int dm_id;
      
    private List<JavaPhoneEvents> listeners;
    
    public DirectMessenger(int id, Boolean is_host, Socket s) throws IOException
    {
        dm_id = id; // TODO: recieve id from database
        this.is_host = is_host;
        source = s;
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }
    
    public void addListener(JavaPhoneEvents to_add)
    {
        listeners.add(to_add);
    }
    
    @Override
    public void run() 
    {
        String msg_type, msg;        
        try 
        {
            while(true)
            {
                msg_type = in.readLine();
                if (msg_type.equals(type_text))
                {
                    msg = in.readLine();
                    for (JavaPhoneEvents l : listeners)
                    {
                        l.handleDM_text(dm_id, source.getInetAddress().toString(), msg);
                    }
                }
            }
        } 
        catch (IOException ex) 
        {
                Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendText(String msg) throws IOException
    {
        out.write(type_text);
        
        out.write(msg);
        out.flush();        
        for (JavaPhoneEvents l : listeners)
        {
            l.handleDM_text(dm_id, "localhost", msg);
        }
    }
}
