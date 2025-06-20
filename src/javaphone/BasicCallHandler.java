/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.example.camera.CameraManager;
import com.livesubtitles.audio.AudioConfig;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
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

    public BasicCallHandler() {
        listeners = new ArrayList<>();
    }

    public void addListener(CallResultHandler to_add) {
        listeners.add(to_add);
    }

    @Override
    public void callRecieved(Handshake hs) {
        int id = MainWindow.db.getDmId(hs.sock.getInetAddress().toString().substring(1));
        if (!hs.message.equals(CallCodes.callPing)) {
            System.out.println("Received call to chat " + String.valueOf(id));
        }
        switch (hs.message) {
            case CallCodes.callDM -> {
                try {
                    DirectMessenger dm = new DirectMessenger(id, true, hs.sock);

                    for (CallResultHandler cr : new ArrayList<>(listeners)) {
                        cr.DMCreated(id, dm);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            case CallCodes.callVoiceVideo -> {
                try {
                    VoiceSender voiceSender = new VoiceSender(hs.sock, AudioConfig.CHUNK_SIZE, hs.dSockSndVoice, hs.voicePort);
                    VoiceReciever voiceReceiver = new VoiceReciever(id, hs.sock, hs.voiceChunkSize, hs.dSockRecVoice);
                    VideoSender videoSender = new VideoSender(hs.sock, CameraManager.chunkSize, hs.dSockSndVideo, hs.videoPort);
                    VideoReciever videoReceiver = new VideoReciever(id, hs.sock, hs.videoChunkSize, hs.dSockRecVideo);
                    System.out.println("Voice port to send: " + String.valueOf(hs.voicePort));
                    System.out.println("Video port to send: " + String.valueOf(hs.videoPort));
                    System.out.println("Voice port to receive: " + String.valueOf(hs.dSockRecVoice.getLocalPort()));
                    System.out.println("Video port to receive: " + String.valueOf(hs.dSockRecVideo.getLocalPort()));
                    for (CallResultHandler cr : new ArrayList<>(listeners)) {
                        cr.VoiceCreated(id, voiceSender, voiceReceiver);
                    }
                    for (CallResultHandler cr : new ArrayList<>(listeners)) {
                        cr.VideoCreated(id, videoSender, videoReceiver);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            case CallCodes.callPing -> {
                String address = hs.sock.getInetAddress().toString().substring(1);

                for (CallResultHandler cr : new ArrayList<>(listeners)) {
                    cr.PingHappened(address, hs.name, hs.sock);
                }
            }
            default -> {
                System.out.println("Unknow call purpose");
            }
        }
    }

    @Override
    public void callSent(Handshake hs) {
        int id = MainWindow.db.getDmId(hs.sock.getInetAddress().toString().substring(1));

        switch (hs.message) {
            case CallCodes.callDM -> {
                try {
                    DirectMessenger dm = new DirectMessenger(id, false, hs.sock);
                    String address = hs.sock.getInetAddress().toString().substring(1);

                    for (CallResultHandler cr : new ArrayList<>(listeners)) {
                        cr.DMCreated(id, dm);
                    }
                    for (CallResultHandler cr : new ArrayList<>(listeners)) {
                        cr.PingHappened(address, hs.name, hs.sock);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            case CallCodes.callVoiceVideo -> {
                try {
                    VoiceSender voiceSender = new VoiceSender(hs.sock, AudioConfig.CHUNK_SIZE, hs.dSockSndVoice, hs.voicePort);
                    VoiceReciever voiceReceiver = new VoiceReciever(id, hs.sock, hs.voiceChunkSize, hs.dSockRecVoice);
                    VideoSender videoSender = new VideoSender(hs.sock, CameraManager.chunkSize, hs.dSockSndVideo, hs.videoPort);
                    VideoReciever videoReceiver = new VideoReciever(id, hs.sock, hs.videoChunkSize, hs.dSockRecVideo);
                    System.out.println("Voice port to send: " + String.valueOf(hs.voicePort));
                    System.out.println("Video port to send: " + String.valueOf(hs.videoPort));
                    System.out.println("Voice port to receive: " + String.valueOf(hs.dSockRecVoice.getLocalPort()));
                    System.out.println("Video port to receive: " + String.valueOf(hs.dSockRecVideo.getLocalPort()));
                    for (CallResultHandler cr : new ArrayList<>(listeners)) {
                        cr.VoiceCreated(id, voiceSender, voiceReceiver);
                    }
                    for (CallResultHandler cr : new ArrayList<>(listeners)) {
                        cr.VideoCreated(id, videoSender, videoReceiver);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(BasicCallHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            case CallCodes.callPing -> {
                String address = hs.sock.getInetAddress().toString().substring(1);

                for (CallResultHandler cr : new ArrayList<>(listeners)) {
                    cr.PingHappened(address, hs.name, hs.sock);
                }
            }
            default -> {
            }
        }
    }

    @Override
    public void callFailed(String ip, String purpose) {
        if (!purpose.equals(CallCodes.callPing)) {
            System.out.println("Failed to call " + ip);
        }

        for (CallResultHandler cr : new ArrayList<>(listeners)) {
            cr.PingHappened(ip, "", null);
        }
    }
}
