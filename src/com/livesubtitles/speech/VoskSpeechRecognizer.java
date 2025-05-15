package com.livesubtitles.speech;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.LibVosk;
import com.livesubtitles.audio.AudioConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import org.vosk.LogLevel;

public class VoskSpeechRecognizer implements SpeechRecognizer {
    private Model model;
    private Recognizer recognizer;
    private SpeechRecognitionListener listener;
    private volatile boolean isRunning;
    private PrintWriter textFileWriter;
    
    public VoskSpeechRecognizer() throws IOException {
        LibVosk.setLogLevel(LogLevel.INFO);
        //String modelPath = "C:\\Users\\paravozik\\Desktop\\java_phone\\audioMan\\audiocapture\\models\\vosk-model-small-en-in-0.4";
        String modelPath = "C:\\Users\\paravozik\\Desktop\\java_phone\\audioMan\\audiocapture\\models\\vosk-model-small-ru-0.22";
        this.model = new Model(modelPath);
        this.recognizer = new Recognizer(model, AudioConfig.SAMPLE_RATE);
        
        // Инициализация файлового писателя
        try {
            this.textFileWriter = new PrintWriter(
                new BufferedWriter(
                    new FileWriter("recognized_text.txt", true) // true для дописывания в файл
                )
            );
        } catch (IOException e) {
            System.err.println("Ошибка при создании файла для записи: " + e.getMessage());
            throw e;
        }
    }
    
    private String fixEncoding(String input) {
        return input;
    }
    
    @Override
    public void startRecognition(SpeechRecognitionListener listener) {
        this.listener = listener;
        this.isRunning = true;
    }
    
    @Override
    public void processAudioChunk(byte[] audioChunk) {
        if (!isRunning || listener == null) return;
        
        try {
            if (recognizer.acceptWaveForm(audioChunk, audioChunk.length)) {
                String result = recognizer.getResult();
                String text = parseResult(result);
                if (!text.isEmpty()) {
                    listener.onFinalResult(text);
                    // Запись завершенной фразы в файл
                    writeToFile(text);
                }
            } else {
                String partial = recognizer.getPartialResult();
                listener.onPartialResult(parsePartial(partial));
            }
        } catch (Exception e) {
            listener.onError(e);
        }
    }
    
    private void writeToFile(String text) {
        if (textFileWriter != null) {
            textFileWriter.println(text);
            textFileWriter.flush(); // Сбрасываем буфер после каждой записи
        }
    }
    
    private String parseResult(String json) {
        try {
            String text = json.replaceAll(".*\"text\"\\s*:\\s*\"([^\"]*)\".*", "$1");
            text = text.replace("{","").replace("}","").replace("\n", "");
            return fixEncoding(text);
        } catch (Exception e) {
            return "Ошибка распознавания";
        }
    }
    
    private String parsePartial(String json) {
        try {
            String partial = json.replaceAll(".*\"partial\"\\s*:\\s*\"([^\"]*)\".*", "$1");
            partial = partial.replace("{","").replace("}","").replace("\n", "");
            return fixEncoding(partial);
        } catch (Exception e) {
            return "...";
        }
    }
    
    @Override
    public void stopRecognition() {
        isRunning = false;
        if (recognizer != null) {
            recognizer.close();
        }
        if (model != null) {
            model.close();
        }
        if (textFileWriter != null) {
            textFileWriter.close();
        }
    }
}