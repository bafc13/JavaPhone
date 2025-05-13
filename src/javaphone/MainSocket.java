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
    private Socket send_request;
    

    public MainSocket() throws IOException
    {
        main_sock = new ServerSocket(PORT);
        send_request = null;
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
        try {
            send_request = new Socket(addr, PORT);
        } catch (IOException ex) {
            Logger.getLogger(MainSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(send_request.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(send_request.getOutputStream()));
            
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
