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

    private final ServerSocket main_sock;
    private List<CallHandler> listeners;


    public MainSocket() throws IOException
    {
        listeners = new ArrayList<>();
        main_sock = new ServerSocket(PORT);
    }

    public void addListener(CallHandler to_add)
    {
        listeners.add(to_add);
    }

    @Override
    public void run()
    {
        BufferedReader in = null;
        BufferedWriter out = null;
        try
        {
            while(true)
            {
                Socket sock = main_sock.accept();
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

                Handshake hs = new Handshake(in.readLine(), in.readLine(), sock);
                System.out.println("Handshake created");
                out.write(CallCodes.responseOK + "\n");
                out.flush();
                out.write(mainJFrame.username + "\n");
                out.flush();
                
                System.out.println("Wrote ok and username");

                if (hs.message.equals(CallCodes.videoCall) || hs.message.equals(CallCodes.voiceCall))
                {
                    int chunkSize = Integer.parseInt(in.readLine());
                    int port = Integer.parseInt(in.readLine());

                    if (hs.message.equals(CallCodes.videoCall))
                    {
                        out.write(String.valueOf(CameraManager.chunkSize) + "\n");
                    }
                    if (hs.message.equals(CallCodes.voiceCall))
                    {
                        out.write(String.valueOf(AudioConfig.CHUNK_SIZE) + "\n");
                    }
                    DatagramSocket dSock = new DatagramSocket();
                    System.out.println(String.valueOf(dSock.getLocalPort()));
                    out.write(String.valueOf(dSock.getLocalPort()) + "\n");
                    out.flush();

                    hs.dSockRecieve = dSock;
                    hs.dSockSend = new DatagramSocket();
                    hs.packetSize = chunkSize;
                    hs.port = port;
                }

                for (CallHandler l : listeners)
                {
                    l.callRecieved(hs);
                }

                in = null;
                out = null;
            }
        }
        catch (IOException e)
        {

            try {
                if (out != null)
                {
                    out.write(CallCodes.responseErr + "\n");
                    out.flush();
                }
                main_sock.close();
            } catch (IOException ex) {
                Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Boolean call(String addr, String name, String purpose)
    {
        Socket sock;
        try {
            sock = new Socket(addr, PORT);
        } catch (IOException ex) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

            out.write(name + "\n");
            out.write(purpose + "\n");
            out.flush();
            if (in.readLine().equals(CallCodes.responseOK))
            {
                String responseName = in.readLine();
                Handshake hs = new Handshake(responseName, purpose, sock);

                if (purpose.equals(CallCodes.videoCall) || purpose.equals(CallCodes.voiceCall))
                {
                    if (purpose.equals(CallCodes.videoCall))
                    {
                        out.write(String.valueOf(CameraManager.chunkSize) + "\n");
                    }
                    if (purpose.equals(CallCodes.voiceCall))
                    {
                        out.write(String.valueOf(AudioConfig.CHUNK_SIZE) + "\n");
                    }
                    DatagramSocket dSock = new DatagramSocket();
                    out.write(String.valueOf(dSock.getLocalPort()) + "\n");
                    out.flush();

                    int chunkSize = Integer.parseInt(in.readLine());
                    int port = Integer.parseInt(in.readLine());

                    hs.dSockRecieve = dSock;
                    hs.dSockSend = new DatagramSocket();
                    hs.packetSize = chunkSize;
                    hs.port = port;
                }

                for (CallHandler l : listeners)
                {
                    l.callSent(hs);
                }

                return true;
            }
            else
            {

            }

        } catch (IOException ex) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return false;
    }
}
