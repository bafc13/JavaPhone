/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.SubtitleHandler;
import javaphone.EventInterfaces.VoiceHandler;

/**
 *
 * @author Andrey
 */
public class VoiceReciever extends Thread {
    private final Socket source;
    private final DataInputStream in;
    
    private List<VoiceHandler> listeners;
    private List<SubtitleHandler> subListeners;
    
    private final int chunk_size;
    private final DatagramSocket dSock;
    private DatagramPacket dPack;
    
    private final int chatID;
    
    public VoiceReciever(int id, Socket s, int cs, DatagramSocket ds) throws IOException
    {
        chatID = id;
        dSock = ds;
        chunk_size = cs;
        source = s;
        in = new DataInputStream(s.getInputStream());
        
        listeners = new ArrayList<>();
        subListeners = new ArrayList<>();
        dPack = new DatagramPacket(new byte[cs], cs);
    }
    
    public void addListener(VoiceHandler to_add)
    {
        listeners.add(to_add);
    }
    
    public void addSubListener(SubtitleHandler to_add) {
        subListeners.add(to_add);
    }
    
    public void receiveSubtitles()
    {   
        String line;
        while (true) {
            try {
                line = in.readUTF();
                for (SubtitleHandler sh : subListeners)
                {
                    sh.SubtitleLineReceived(chatID, source.getInetAddress().toString(), line);
                }
            } catch (IOException ex) {
                Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void run()
    {
        Runnable task = () -> {
		receiveSubtitles();
	};
	Thread subtitleThread = new Thread(task);
        subtitleThread.start();

        while (true)
        {
            try {
                dSock.receive(dPack);
                
                for (VoiceHandler l : listeners)                  
                {
                    l.HandleVoiceRecieved(chatID, source.getInetAddress().toString(), dPack.getData());
                }
            } catch (IOException ex) {
                Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
