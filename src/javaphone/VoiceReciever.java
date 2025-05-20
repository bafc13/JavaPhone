/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey
 */
public class VoiceReciever extends Thread {
    private final Socket source;
    private final DataInputStream in;
    private List<JavaPhoneEvents> listeners;
    private final int chunk_size;
    
    public VoiceReciever(Socket s, int cs) throws IOException
    {
        chunk_size = cs;
        source = s;
        in = new DataInputStream(s.getInputStream());
    }
    
    @Override
    public void run()
    {
        int bytes_read;
        byte[] chunk = new byte[chunk_size];
        while (true)
        {
            try {
                bytes_read = in.read(chunk);
                
                for (JavaPhoneEvents l : listeners)                  
                {
                    l.handleVoiceRecieved(source.getInetAddress().toString(), source.getInetAddress().toString(), chunk);
                }
            } catch (IOException ex) {
                Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
