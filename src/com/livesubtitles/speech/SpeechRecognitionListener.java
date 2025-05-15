// SpeechRecognitionListener.java
package com.livesubtitles.speech;

public interface SpeechRecognitionListener {
    void onPartialResult(String transcript);
    void onFinalResult(String transcript);
    void onError(Exception e);
}