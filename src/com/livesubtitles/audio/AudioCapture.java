package com.livesubtitles.audio;

import javax.sound.sampled.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioCapture {
    private TargetDataLine microphone;
    private BlockingQueue<byte[]> audioQueue;
    private volatile boolean isRecording;
    
    public AudioCapture() {
        this.audioQueue = new LinkedBlockingQueue<>();
    }
    
    public void startCapture() throws LineUnavailableException {
        AudioFormat format = AudioConfig.getAudioFormat();
        System.out.println("Используемый аудиоформат: " + format);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Микрофон не поддерживается с текущим форматом");
        }
        
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();
        
        isRecording = true;
        new Thread(this::captureLoop).start();
    }
    
    private void captureLoop() {
        byte[] buffer = new byte[AudioConfig.CHUNK_SIZE];
        while (isRecording) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                audioQueue.add(buffer.clone());
            }
        }
    }
    
    public byte[] getAudioChunk() throws InterruptedException {
        return audioQueue.take();
    }
    
    
    
    public void stopCapture() {
        isRecording = false;
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
    }
    
    public boolean isRecording() {
        return isRecording;
    }
}