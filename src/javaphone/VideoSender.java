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
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Andrey
 */
public class VideoSender extends Thread {
    public static final int w = 1280;
    public static final int h = 720;
    
    private final Socket source;
    private final DataOutputStream out;

    private CameraManager cam;

    private List<JavaPhoneEvents> listeners;

    public VideoSender(Socket s, int cs) throws IOException {
        source = s;
        out = new DataOutputStream(s.getOutputStream());
    }

    public void addListener(JavaPhoneEvents to_add) {
        listeners.add(to_add);
    }

    @Override
    public void run() {
        byte[] chunk;
        BufferedImage frame = cam.getCurrentFrame();
        while (true) {
            try {
                chunk = convertToSend(frame);
                out.write(chunk);

                for (JavaPhoneEvents l : listeners) {
                    l.handleCameraFrameSent(source.getInetAddress().toString(), source.getInetAddress().toString(), frame);
                }

            } catch (Exception ex) {
                Logger.getLogger(VoiceReciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private byte[] convertToSend(BufferedImage frame) throws IOException 
    {
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
}
