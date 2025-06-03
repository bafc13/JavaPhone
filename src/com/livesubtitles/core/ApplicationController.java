package com.livesubtitles.core;

import com.livesubtitles.audio.AudioCapture;
import com.livesubtitles.speech.SpeechRecognitionListener;
import com.livesubtitles.speech.SpeechRecognizer;
import com.livesubtitles.ui.SubtitleDisplay;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import javaphone.EventInterfaces.SubtitleHandler;
import java.util.List;
import javaphone.EventInterfaces.CallResultHandler;

public class ApplicationController implements SpeechRecognitionListener {
    private final AudioCapture audioCapture;
    private final SpeechRecognizer speechRecognizer;
    private final SubtitleDisplay subtitleDisplay;
    private Thread processingThread;
    private Timer resultDisplayTimer;
    private AtomicReference<String> lastFinalResult = new AtomicReference<>("");
    private volatile boolean showingFinalResult = false;
    private List<SubtitleHandler> listeners;

    public ApplicationController(SpeechRecognizer speechRecognizer, SubtitleDisplay subtitleDisplay) {
        listeners = new ArrayList<>();
        this.audioCapture = new AudioCapture();
        this.speechRecognizer = speechRecognizer;
        this.subtitleDisplay = subtitleDisplay;
        
        this.resultDisplayTimer = new Timer(2000, e -> {
            showingFinalResult = false;
            SwingUtilities.invokeLater(() -> {
                if (lastFinalResult.get().isEmpty()) {
                    subtitleDisplay.updateText("", false);
                }
            });
        });
        this.resultDisplayTimer.setRepeats(false);
    }
    
    public void addListener(SubtitleHandler to_add)
    {
        listeners.add(to_add);
    }
    
    public void start() {
        try {
            speechRecognizer.startRecognition(this);
            audioCapture.startCapture();
            
            processingThread = new Thread(this::processAudio);
            processingThread.start();
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> onError(e));
        }
    }

    private void processAudio() {
        while (audioCapture.isRecording()) {
            try {
                byte[] audioChunk = audioCapture.getAudioChunk(); 
                speechRecognizer.processAudioChunk(audioChunk);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public AudioCapture getMic()
    {
        return audioCapture;
    }

    public void stop() {
        
        audioCapture.stopCapture();
        speechRecognizer.stopRecognition();
        resultDisplayTimer.stop();
        if (processingThread != null) {
            processingThread.interrupt();
        }
    }

    @Override
    public void onPartialResult(String transcript) {
        if (isNullOrEmpty(transcript) || showingFinalResult) {
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            subtitleDisplay.updateText(transcript, false);
        });
    }

    @Override
    public void onFinalResult(String transcript) {
        if (isNullOrEmpty(transcript)) {
            return;
        }
        
        for (SubtitleHandler sh : listeners)
        {
            sh.SubtitleLineRecorded(transcript);
        }
        
        lastFinalResult.set(transcript);
        showingFinalResult = true;
        resultDisplayTimer.restart();
        
        SwingUtilities.invokeLater(() -> {
            subtitleDisplay.updateText(transcript, true);
        });
    }

    @Override
    public void onError(Exception e) {
        SwingUtilities.invokeLater(() -> {
            System.err.println("Error: " + e.getMessage());
            subtitleDisplay.updateText("Ошибка: " + e.getMessage(), true);
        });
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty() || text.equals("{}") || text.trim().equals("\"\"");
    }
}