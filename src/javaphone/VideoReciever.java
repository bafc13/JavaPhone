/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
import javaphone.EventInterfaces.VideoHandler;
import javax.imageio.ImageIO;

/**
 *
 * @author Andrey
 */
public class VideoReciever extends Thread {

    private int recievedSize;

    private final Socket source;
    private final DataInputStream in;
    private List<VideoHandler> listeners;
    
    private final int chunk_size;
    private final DatagramSocket dSock;
    private DatagramPacket dPack;
    
    private final int chatID;

    public VideoReciever(int id, Socket s, int cs, DatagramSocket ds) throws IOException {
        chatID = id;
        dSock = ds;
        chunk_size = cs;
        source = s;
        in = new DataInputStream(s.getInputStream());
        
        listeners = new ArrayList<>();
        dPack = new DatagramPacket(new byte[cs], cs);
    }

    public void addListener(VideoHandler to_add) {
        listeners.add(to_add);
    }
    
    
    @Override
    public void run() {
        
        int bytesRead;
        byte[] chunk;
        BufferedImage frame;
        while (true) {
            try {
                dSock.receive(dPack);
                if (dPack.getData().length != 0) {
                    chunk = dPack.getData();
                    frame = convertRecieved(chunk);

                    for (VideoHandler l : listeners) {
                        l.HandleCameraFrameRecieved(chatID, source.getInetAddress().toString().substring(1), frame);
                    }
                } else {
                    for (VideoHandler l : listeners) {
                        l.HandleCameraFrameRecieved(chatID, source.getInetAddress().toString().substring(1), null);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private BufferedImage convertRecieved(byte[] frame) {
        ByteArrayInputStream bais = new ByteArrayInputStream(frame);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
