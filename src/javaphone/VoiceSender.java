/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.livesubtitles.audio.AudioCapture;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.SubtitleHandler;
import javaphone.EventInterfaces.VoiceHandler;

/**
 *
 * @author Andrey
 */
public class VoiceSender implements VoiceHandler, SubtitleHandler {

    private final Socket source;
    private final DataOutputStream out;
    private final int chunk_size;
    private final DatagramSocket dSock;
    private DatagramPacket dPack;
    private final int port;

    public VoiceSender(Socket s, int cs, DatagramSocket ds, int port) throws IOException {
        this.port = port;
        dSock = ds;
        chunk_size = cs;
        source = s;
        out = new DataOutputStream(s.getOutputStream());
    }

    @Override
    public void HandleVoiceRecieved(int chatID, String address, byte[] audioChunk) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void HandleVoiceRecorded(byte[] audioChunk) {
        dPack = new DatagramPacket(audioChunk, chunk_size, source.getInetAddress(), port);
        try {
            dSock.send(dPack);
        } catch (Exception ex) {
            Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void SubtitleLineRecorded(String line) {
        try {
            out.writeUTF(line);
        } catch (IOException ex) {
            Logger.getLogger(VoiceSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void SubtitleLineReceived(int chatID, String address, String line) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
