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
import javaphone.EventInterfaces.DMHandler;


/**
 *
 * @author Andrey
 */
public class DirectMessenger extends Thread {
    //Andrey lox

    private final Boolean is_host;
    private final Socket source;
    private final DataInputStream in;
    private final DataOutputStream out;
    public static int type_text = 100;
    public static int type_file = 200;
    public int dm_id;

    private List<DMHandler> listeners;

    public DirectMessenger(int id, Boolean is_host, Socket s) throws IOException
    {
        dm_id = id; // TODO: recieve id from database
        this.is_host = is_host;
        source = s;
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
        
        listeners = new ArrayList<>();
    }

    public void addListener(DMHandler to_add)
    {
        listeners.add(to_add);
    }

    private String readTextMessage()
    {
        try {
            String msg = in.readUTF();
            return msg;
        } catch (IOException ex) {
            Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String readFile()
    {
        try {
            int msg_size = in.readInt();
            byte[] b = new byte[msg_size];
            int bytes_read = in.read(b, 0, msg_size);
            String fname = new String(b);

            long size = in.readLong();
            int bytes = 0;
            FileOutputStream fs = new FileOutputStream("files/" + fname);

            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {

                fs.write(buffer, 0, bytes);
                size -= bytes;
            }
            fs.close();

            return fname;
        } catch (IOException ex) {
            Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void run()
    {
        int msg_type;

        byte[] b;
        String msg;
        try
        {
            while(true)
            {
                msg_type = in.readInt();
                if (msg_type == type_text)
                {
                    msg = readTextMessage();
                    for (DMHandler l : listeners)
                    {
                        l.HandleDMText(source.getInetAddress().toString(), source.getInetAddress().toString(), msg);
                    }
                }
                else if (msg_type == type_file)
                {
                    msg = readFile();
                    for (DMHandler l : listeners)
                    {
                        l.HandleDMFile(source.getInetAddress().toString(), source.getInetAddress().toString(), msg);
                    }
                }
            }
        }
        catch (IOException ex)
        {
                Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendText(String msg) throws IOException
    {
        out.writeInt(type_text);
        out.writeUTF(msg);
        System.out.println(msg.length());
        out.flush();

        for (DMHandler l : listeners)
        {
            l.HandleDMText(source.getInetAddress().toString(), "localhost", msg);
        }
    }

    public void sendFile(String path, String fname) throws Exception
    {
        out.writeInt(type_file);
        int bytes = 0;
        // Open the File where he located in your pc
        File file = new File(path + "/" + fname);
        FileInputStream fs = new FileInputStream(file);

        out.writeInt(fname.length());
        out.writeBytes(fname);
        // Here we send the File to Server
        out.writeLong(file.length());
        // Here we  break file into chunks
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fs.read(buffer))
               != -1) {
            out.write(buffer, 0, bytes);
            out.flush();
        }
        // close the file here
        fs.close();
    }
}
