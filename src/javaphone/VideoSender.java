/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import com.example.camera.CameraManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.SubtitleHandler;
import javaphone.EventInterfaces.VideoHandler;
import javax.imageio.ImageIO;

/**
 *
 * @author Andrey
 */
public class VideoSender implements VideoHandler, SubtitleHandler {

    public static final int w = 1280;
    public static final int h = 720;
    private int chunk_size;
    private final Socket source;
    private final DataOutputStream out;
    private final DatagramSocket dSock;
    private DatagramPacket dPack;
    private final int port;

    public VideoSender(Socket s, int cs, DatagramSocket ds, int port) throws IOException {
        chunk_size = cs;
        dSock = ds;
        this.port = port;
        source = s;
        out = new DataOutputStream(s.getOutputStream());
    }

    private byte[] convertToSend(BufferedImage frame) throws IOException {
        BufferedImage img
                = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = frame.getWidth();
        int hh = frame.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++) {
            ys[y] = y * hh / h;
        }
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = frame.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);
        return baos.toByteArray();
    }

    @Override
    public void HandleCameraFrameRecieved(int chatID, String address, BufferedImage frame) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void HandleCameraFrameRecorded(BufferedImage frame) {
        System.out.println("Sending frame");
        try {
            if (frame != null) {
                dPack = new DatagramPacket(convertToSend(frame), chunk_size, source.getInetAddress(), port);
                dSock.send(dPack);
            }
            else
            {
                dPack = new DatagramPacket(new byte[0], 0, source.getInetAddress(), port);
                dSock.send(dPack);
            }
        } catch (Exception ex) {
            Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void SubtitleLineRecorded(String line) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void SubtitleLineReceived(int chatID, String address, String line) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
