/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.livesubtitles.audio.AudioConfig;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.CallHandler;
import javaphone.EventInterfaces.CallResultHandler;

/**
 *
 * @author Andrey
 */
public class BasicCallHandler implements CallHandler {
    private List<CallResultHandler> listeners;
    
    public BasicCallHandler()
    {
    }
    
    public void addListener(CallResultHandler to_add)
    {
        listeners.add(to_add);
    }
    
    @Override
    public void callRecieved(Handshake hs) {
        int id = mainJFrame.db.getDmId(hs.sock.getInetAddress().toString());
        
        if (hs.message.equals(CallCodes.dm))
        {
            try {
                DirectMessenger dm = new DirectMessenger(id, true, hs.sock);
                
                for (CallResultHandler cr : listeners)
                {
                    cr.DMCreated(dm);
                }
            } catch (IOException ex) {
                Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (hs.message.equals(CallCodes.voiceCall))
        {
            try {
                VoiceSender vs = new VoiceSender(hs.sock, AudioConfig.CHUNK_SIZE);
                VoiceReciever vr = new VoiceReciever(hs.sock, AudioConfig.CHUNK_SIZE);
                
                for (CallResultHandler cr : listeners)
                {
                    cr.VoiceCreated(vs, vr);
                }
            } catch (IOException ex) {
                Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void callSent(Handshake hs) {
        int id = mainJFrame.db.getDmId(hs.sock.getInetAddress().toString());
        
        if (hs.message.equals(CallCodes.dm))
        {
            try {
                DirectMessenger dm = new DirectMessenger(id, false, hs.sock);
                
                for (CallResultHandler cr : listeners)
                {
                    cr.DMCreated(dm);
                }
            } catch (IOException ex) {
                Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (hs.message.equals(CallCodes.voiceCall))
        {
            try {
                VoiceSender vs = new VoiceSender(hs.sock, AudioConfig.CHUNK_SIZE);
                VoiceReciever vr = new VoiceReciever(hs.sock, AudioConfig.CHUNK_SIZE);
                
                for (CallResultHandler cr : listeners)
                {
                    cr.VoiceCreated(vs, vr);
                }
            } catch (IOException ex) {
                Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }   
}
