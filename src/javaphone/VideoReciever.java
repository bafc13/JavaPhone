/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public VideoReciever(Socket s, int cs) throws IOException {
        chunk_size = cs;
        source = s;
        in = new DataInputStream(s.getInputStream());
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
                recievedSize = in.readInt();
                if (recievedSize != 0) {
                    chunk = new byte[recievedSize];
                    bytesRead = in.read(chunk);
                    frame = convertRecieved(chunk);

                    for (VideoHandler l : listeners) {
                        l.HandleCameraFrameRecieved(source.getInetAddress().toString(), source.getInetAddress().toString(), frame);
                    }
                } else {
                    for (VideoHandler l : listeners) {
                        l.HandleCameraFrameRecieved(source.getInetAddress().toString(), source.getInetAddress().toString(), null);
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
