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

/**
 *
 * @author Andrey
 */
public class MainSocket extends Thread {

    public static final int PORT = 666;

    private final ServerSocket mainSock;
    private List<CallHandler> listeners;

    public MainSocket() throws IOException {
        listeners = new ArrayList<>();
        mainSock = new ServerSocket(PORT);
    }

    public void addListener(CallHandler to_add) {
        listeners.add(to_add);
    }

    @Override
    public void run() {
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            while (true) {
                Handshake hs;
                Socket sock = mainSock.accept();
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                String responseName = in.readLine();
                String purpose = in.readLine();

                if (!purpose.equals(CallCodes.callPing)) {
                    out.write(CallCodes.responseWait + "\n");
                    out.flush();
                    NotificationDialog nd = new NotificationDialog(JavaPhone.frame, purpose, responseName);
                    boolean res = nd.getResponse();
                    if (!res) {
                        out.write(CallCodes.responseRefuze + "\n");
                        in.close();
                        out.close();
                        continue;
                    }
                }

                out.write(CallCodes.responseAccept + "\n");
                out.flush();
                out.write(mainJFrame.username + "\n");
                out.flush();

                if (purpose.equals(CallCodes.callVideo) || purpose.equals(CallCodes.callVoice)) {
                    int chunkSize = Integer.parseInt(in.readLine());
                    int port = Integer.parseInt(in.readLine());

                    if (purpose.equals(CallCodes.callVideo)) {
                        out.write(String.valueOf(CameraManager.chunkSize) + "\n");
                    }
                    if (purpose.equals(CallCodes.callVoice)) {
                        out.write(String.valueOf(AudioConfig.CHUNK_SIZE) + "\n");
                    }
                    DatagramSocket dSockRec = new DatagramSocket();
                    DatagramSocket dSockSend = new DatagramSocket();
                    out.write(String.valueOf(dSockRec.getLocalPort()) + "\n");
                    out.flush();

                    hs = new Handshake(responseName, purpose, sock, dSockRec, dSockSend, port, chunkSize);
                } else {
                    hs = new Handshake(responseName, purpose, sock);
                }

                for (CallHandler l : listeners) {
                    l.callRecieved(hs);
                }
            }
        } catch (IOException e) {

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
        System.out.println(task.status);
        if (task.status) {
            for (CallHandler l : listeners) {
                l.callSent(task.result);
            }
        } else {
            for (CallHandler l : listeners) {
                l.callFailed(addr);
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

        public static final long delayOffline = 5000L;
        public static final long delayResponse = 60000L;

        public CallTask(String addr, String name, String purpose) {
            this.addr = addr;
            this.name = name;
            this.purpose = purpose;

            status = false;
        }

        @Override
        public void run() {
            TimerTask interruptTask1 = new TimerTask() {
                public void run() {
                    finish();
                }
            ;
            };
            TimerTask interruptTask2 = new TimerTask() {
                public void run() {
                    finish();
                }
            ;
            };
            Timer interruptOfflineTimer = new Timer();
            interruptOfflineTimer.schedule(interruptTask1, delayOffline);
            Timer interruptResponseTimer = new Timer();
            interruptResponseTimer.schedule(interruptTask2, delayResponse);

            Socket sock;
            try {
                sock = new Socket(addr, PORT);
            } catch (IOException ex) {
                Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
                finish();
                return;
            }

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

                out.write(name + "\n");
                out.write(purpose + "\n");
                out.flush();

                if (!purpose.equals(CallCodes.callPing) && in.readLine().equals(CallCodes.responseWait)) {
                    interruptOfflineTimer.cancel();
                }

                if (in.readLine().equals(CallCodes.responseAccept)) {
                    String responseName = in.readLine();

                    if (purpose.equals(CallCodes.callVideo) || purpose.equals(CallCodes.callVoice)) {
                        if (purpose.equals(CallCodes.callVideo)) {
                            out.write(String.valueOf(CameraManager.chunkSize) + "\n");
                        }
                        if (purpose.equals(CallCodes.callVoice)) {
                            out.write(String.valueOf(AudioConfig.CHUNK_SIZE) + "\n");
                        }
                        DatagramSocket dSockRec = new DatagramSocket();
                        DatagramSocket dSockSend = new DatagramSocket();
                        out.write(String.valueOf(dSockRec.getLocalPort()) + "\n");
                        out.flush();

                        int chunkSize = Integer.parseInt(in.readLine());
                        int port = Integer.parseInt(in.readLine());
                        result = new Handshake(responseName, purpose, sock, dSockRec, dSockSend, port, chunkSize);
                    } else {
                        result = new Handshake(responseName, purpose, sock);
                    }
                    status = true;
                    finish();
                } else {
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
