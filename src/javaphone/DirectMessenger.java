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
import javaphone.EventInterfaces.NotificationHandler;

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
    public int chatID;
    public String senderIP;

    private List<DMHandler> listeners;
    private List<NotificationHandler> notificationListeners;

    public DirectMessenger(int id, Boolean is_host, Socket s) throws IOException {
        chatID = id;
        this.is_host = is_host;
        source = s;
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());

        senderIP = source.getInetAddress().toString().substring(1);

        listeners = new ArrayList<>();
        notificationListeners = new ArrayList<>();
    }

    public void addListener(DMHandler to_add) {
        listeners.add(to_add);
    }

    public void addNotificationListener(NotificationHandler to_add) {
        notificationListeners.add(to_add);
    }

    private String readTextMessage() {
        try {
            String msg = in.readUTF();
            return msg;
        } catch (IOException ex) {
            Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String readFile() {
        try {
            String hash = in.readUTF();
            String path = MainWindow.db.findFileWithChecksum(hash);
            if (path.equals("")) {
                out.writeUTF(CallCodes.fileRequired);
                out.flush();
                
                String fname = in.readUTF();

                long size = in.readLong();
                int bytes = 0;
                FileOutputStream fs = new FileOutputStream("./files/" + fname);

                byte[] buffer = new byte[4 * 1024];
                while (size > 0 && (bytes = in.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {

                    fs.write(buffer, 0, bytes);
                    size -= bytes;
                }
                fs.close();

                return "./files/" + fname;
            } else {
                out.writeUTF(CallCodes.filePresent);
                out.flush();
                return path;
            }
        } catch (IOException ex) {
            Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void run() {
        int msg_type;

        byte[] b;
        String msg;
        try {
            while (true) {
                msg_type = in.readInt();
                if (msg_type == type_text) {
                    msg = readTextMessage();
                    for (DMHandler l : listeners) {
                        l.HandleDMText(chatID, senderIP, msg);
                    }
                    for (NotificationHandler nh : notificationListeners) {
                        nh.messageReceived(chatID, senderIP, msg, false);
                    }
                } else if (msg_type == type_file) {
                    msg = readFile();
                    for (DMHandler l : listeners) {
                        l.HandleDMFile(chatID, senderIP, msg);
                    }
                    for (NotificationHandler nh : notificationListeners) {
                        nh.messageReceived(chatID, senderIP, msg, true);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DirectMessenger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getID() {
        return chatID;
    }

    public String getIP() {
        return source.getInetAddress().toString().substring(1);
    }

    public void sendText(String msg) throws IOException {
        out.writeInt(type_text);
        out.writeUTF(msg);
        out.flush();

        for (DMHandler l : listeners) {
            l.HandleDMText(chatID, "localhost", msg);
        }
    }

    public void sendFile(String path, String fname) throws Exception {
        MainWindow.db.addFile(path + "/" + fname);

        out.writeInt(type_file);
        out.flush();
        String hash = MainWindow.db.countChecksum("./files/" + fname);
        System.out.println(hash);
        out.writeUTF("huesos");
        out.flush();
        String response = in.readUTF();
        
        if (response.equals(CallCodes.fileRequired)) {
            System.out.println("File required, sending");
            int bytes = 0;
            // Open the File where he located in your pc
            File file = new File(path + "/" + fname);
            FileInputStream fs = new FileInputStream(file);

            out.writeUTF(fname);
            // Here we send the File to Server
            out.writeLong(file.length());
            // Here we  break file into chunks
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fs.read(buffer))
                    != -1) {
                out.write(buffer, 0, bytes);
                out.flush();
            }
            System.out.println("Wrote file");
            // close the file here
            fs.close();
        } else {
            System.out.println("File is not needed");
        }

        for (DMHandler l : listeners) {
            l.HandleDMFile(chatID, "localhost", "./files/" + fname);
        }
    }
}
