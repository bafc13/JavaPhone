/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.livesubtitles.audio.AudioCapture;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey
 */
public class VoiceSender extends Thread {
    private final Socket source;
    private final DataOutputStream out;
    private List<JavaPhoneEvents> listeners;
    private final int chunk_size;
    
    private AudioCapture mic;
    
    public VoiceSender(Socket s, int cs) throws IOException
    {
        chunk_size = cs;
        source = s;
        out = new DataOutputStream(s.getOutputStream());
    }
    
    public void addListener(JavaPhoneEvents to_add)
    {
        listeners.add(to_add);
    }
    
    @Override
    public void run()
    {
        byte[] chunk;
        while (true)
        {
            try {
                chunk = mic.getAudioChunk();
                out.write(chunk);
                
                for (JavaPhoneEvents l : listeners)                  
                {
                    l.handleVoiceSent(source.getInetAddress().toString(), source.getInetAddress().toString(), chunk);
                }
                
            } catch (Exception ex) {
                Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
