package com.livesubtitles.speech;

public interface SpeechRecognizer {
    void startRecognition(SpeechRecognitionListener listener);
    void processAudioChunk(byte[] audioChunk);
    void stopRecognition();
}