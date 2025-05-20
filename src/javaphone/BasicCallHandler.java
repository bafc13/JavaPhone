/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.livesubtitles.audio.AudioConfig;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey
 */
public class BasicCallHandler implements JavaPhoneEvents {
    private DBManager dbm;
    private List<CallResults> listeners;
    
    public BasicCallHandler(DBManager dbm)
    {
        this.dbm = dbm;
    }
    
    public void addListener(CallResults to_add)
    {
        listeners.add(to_add);
    }
    
    @Override
    public void callRecieved(Handshake hs) {
        int id = dbm.get_dm_id(hs.sock.getInetAddress().toString());
        
        if (hs.message.equals(CallCodes.dm))
        {
            try {
                DirectMessenger dm = new DirectMessenger(id, true, hs.sock);
                
                for (CallResults cr : listeners)
                {
                    cr.dm_created(dm);
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
                
                for (CallResults cr : listeners)
                {
                    cr.voice_created(vs, vr);
                }
            } catch (IOException ex) {
                Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void callSent(Handshake hs) {
        int id = dbm.get_dm_id(hs.sock.getInetAddress().toString());
        
        if (hs.message.equals(CallCodes.dm))
        {
            try {
                DirectMessenger dm = new DirectMessenger(id, false, hs.sock);
                
                for (CallResults cr : listeners)
                {
                    cr.dm_created(dm);
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
                
                for (CallResults cr : listeners)
                {
                    cr.voice_created(vs, vr);
                }
            } catch (IOException ex) {
                Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void handleDM_text(String dm_address, String address, String text) {
        // Has nothing to do with calls
    }

    @Override
    public void handleDM_file(String dm_address, String address, String fname) {
        // Has nothing to do with calls
    }

    @Override
    public void handleVoiceRecieved(String dm_address, String address, byte[] audioChunk) {
        // Has nothing to do with calls
    }

    @Override
    public void handleVoiceSent(String dm_address, String address, byte[] audioChunk) {
        // Has nothing to do with calls
    }
    
}
