/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;


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
        listeners = new ArrayList<CallHandler>();
        main_sock = new ServerSocket(PORT);
    }

    public void addListener(CallHandler to_add)
    {
        listeners.add(to_add);
    }

    @Override
    public void run()
    {
        try
        {
            while(true)
            {
                Socket sock = main_sock.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

                Handshake hs = new Handshake(in.readLine(), in.readLine(), sock);
                System.out.println("Zvonit huesos");
                System.out.println("ALLO NHAUYAFDAJHGBFJAHGFJHAGFJKHSG");
                System.out.println(hs.message);
                System.out.println(hs.name);
                out.write("OK");
                out.flush();

                for (CallHandler l : listeners)
                {
                    l.callRecieved(hs);
                }
            }
        }
        catch (IOException e)
        {

            try {
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
            System.out.println("HUYAKA BLYAT");
        } catch (IOException ex) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

            out.write(name + "\n");
            out.write(purpose);
            out.flush();

            if (in.readLine().equals("OK"))
            {
                System.out.println("Otvetili");
                Handshake hs = new Handshake(name, purpose, sock);

                for (CallHandler l : listeners)
                {
                    l.callSent(hs);
                }

                return true;
            }
            else
            {
                System.out.println("Ne otvechaet pidoras");
            }

        } catch (IOException ex) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return false;
    }
}
