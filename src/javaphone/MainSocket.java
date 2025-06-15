/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.example.camera.CameraManager;
import com.livesubtitles.audio.AudioConfig;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.CallHandler;
import javaphone.EventInterfaces.NotificationHandler;

/**
 *
 * @author Andrey
 */
public class MainSocket extends Thread {

    public static final int PORT = 666;

    private final ServerSocket mainSock;
    private List<CallHandler> listeners;
    private List<NotificationHandler> notificationListeners;

    public MainSocket() throws IOException {
        listeners = new ArrayList<>();
        notificationListeners = new ArrayList<>();

        mainSock = new ServerSocket(PORT);
    }

    public void addListener(CallHandler to_add) {
        listeners.add(to_add);
    }

    public void addNotificationListener(NotificationHandler to_add) {
        notificationListeners.add(to_add);
    }

    @Override
    public void run() {
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            while (true) {
                Handshake hs;
                Socket sock = mainSock.accept();
                String ip = sock.getInetAddress().toString().substring(1);

                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                String responseName = in.readLine();
                String purpose = in.readLine();

                if (purpose.equals(CallCodes.callVoiceVideo)) {
                    out.write(CallCodes.responseWait + "\n");
                    out.flush();

                    boolean res = true;
                    for (NotificationHandler nh : notificationListeners) {
                        res = res && nh.callReceived(MainWindow.db.getDmId(ip), ip);
                    }

                    if (!res) {
                        System.out.println("Refuzed");
                        out.write(CallCodes.responseRefuze);
                        out.flush();

                        in.close();
                        out.close();
                        sock.close();

                        continue;
                    } else {

                        System.out.println("ACCEPTED CALL");
                    }

                }

                out.write(CallCodes.responseAccept + "\n");
                out.flush();
                out.write(MainWindow.username + "\n");
                out.flush();

                if (purpose.equals(CallCodes.callVoiceVideo)) {
                    int voiceChunkSize = Integer.parseInt(in.readLine());
                    int videoChunkSize = Integer.parseInt(in.readLine());
                    int voicePort = Integer.parseInt(in.readLine());
                    int videoPort = Integer.parseInt(in.readLine());

                    out.write(String.valueOf(AudioConfig.CHUNK_SIZE) + "\n");
                    out.write(String.valueOf(CameraManager.chunkSize) + "\n");

                    DatagramSocket dSockRecVoice = new DatagramSocket();
                    DatagramSocket dSockSndVoice = new DatagramSocket();
                    DatagramSocket dSockRecVideo = new DatagramSocket();
                    DatagramSocket dSockSndVideo = new DatagramSocket();

                    out.write(String.valueOf(dSockRecVoice.getLocalPort()) + "\n");
                    out.write(String.valueOf(dSockRecVideo.getLocalPort()) + "\n");
                    out.flush();

                    hs = new Handshake(responseName, purpose, sock, dSockRecVoice, dSockSndVoice, dSockRecVideo, dSockSndVideo, voicePort, videoPort, voiceChunkSize, videoChunkSize);
                } else {
                    hs = new Handshake(responseName, purpose, sock);
                }

                for (CallHandler l : new ArrayList<>(listeners)) {
                    l.callRecieved(hs);
                }
            }
        } catch (IOException e) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, e);
            try {
                if (out != null) {
                    out.write(CallCodes.responseError + "\n");
                    out.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized Boolean call(String addr, String name, String purpose) {
        CallTask task = new CallTask(addr, name, purpose);
        task.start();
        try {
            wait();
        } catch (InterruptedException ex) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        task.interrupt();
        task.interruptTimerOffline.cancel();
        // System.out.println(task.status);
        if (task.status) {
            for (CallHandler l : new ArrayList<>(listeners)) {
                l.callSent(task.result);
            }
        } else {
            for (CallHandler l : new ArrayList<>(listeners)) {
                l.callFailed(addr, purpose);
            }
        }

        return task.status;
    }

    private class CallTask extends Thread {

        private final String addr;
        private final String name;
        private final String purpose;

        public Boolean status;
        public Handshake result;
        public Timer interruptTimerOffline;
        public Timer interruptTimerRefuze;

        public CallTask(String addr, String name, String purpose) {
            this.addr = addr;
            this.name = name;
            this.purpose = purpose;

            status = false;
        }

        @Override
        public void run() {
            TimerTask interruptTask1 = new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            ;
            };
            TimerTask interruptTask2 = new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            ;
            };

            interruptTimerOffline = new Timer();
            interruptTimerOffline.schedule(interruptTask1, CallCodes.delayOffline);
            interruptTimerRefuze = new Timer();
            interruptTimerRefuze.schedule(interruptTask2, CallCodes.delayResponse);

            Socket sock;
            try {
                sock = new Socket(addr, PORT);
            } catch (IOException ex) {
                //Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
                finish();
                return;
            }

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

                out.write(name + "\n");
                out.write(purpose + "\n");
                out.flush();

                if (purpose.equals(CallCodes.callVoiceVideo) && in.readLine().equals(CallCodes.responseWait)) {
                    interruptTimerOffline.cancel();
                    interruptTimerOffline.purge();
                }

                if (in.readLine().equals(CallCodes.responseAccept)) {
                    String responseName = in.readLine();

                    if (purpose.equals(CallCodes.callVoiceVideo)) {
                        out.write(String.valueOf(AudioConfig.CHUNK_SIZE) + "\n");
                        out.write(String.valueOf(CameraManager.chunkSize) + "\n");

                        DatagramSocket dSockRecVoice = new DatagramSocket();
                        DatagramSocket dSockSndVoice = new DatagramSocket();
                        DatagramSocket dSockRecVideo = new DatagramSocket();
                        DatagramSocket dSockSndVideo = new DatagramSocket();

                        out.write(String.valueOf(dSockRecVoice.getLocalPort()) + "\n");
                        out.write(String.valueOf(dSockRecVideo.getLocalPort()) + "\n");
                        out.flush();

                        int voiceChunkSize = Integer.parseInt(in.readLine());
                        int videoChunkSize = Integer.parseInt(in.readLine());
                        int voicePort = Integer.parseInt(in.readLine());
                        int videoPort = Integer.parseInt(in.readLine());

                        result = new Handshake(responseName, purpose, sock, dSockRecVoice, dSockSndVoice, dSockRecVideo, dSockSndVideo, voicePort, videoPort, voiceChunkSize, videoChunkSize);
                    } else {
                        result = new Handshake(responseName, purpose, sock);
                    }
                    status = true;
                    finish();
                } else {
                    in.close();
                    out.close();
                    sock.close();
                    finish();
                }
            } catch (IOException ex) {
                Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
                finish();
            }
        }

    }

    private synchronized void finish() {
        notify();
    }

}
