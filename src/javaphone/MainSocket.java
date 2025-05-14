/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;


import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Andrey
 */
public class MainSocket extends Thread {
    public static final int PORT = 666;
    private final ServerSocket main_sock;
    

    public MainSocket() throws IOException
    {
        main_sock = new ServerSocket(PORT);
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
                
                Handshake hs = new Handshake(in.readLine(), sock);
                out.write("OK");
                out.flush();
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
    
    public Boolean call(String addr, String purpose)
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
            
            out.write(purpose);
            out.flush();
            
            if (in.readLine().equals("OK"))
                return true;
            
        } catch (IOException ex) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return false;
    }
}
