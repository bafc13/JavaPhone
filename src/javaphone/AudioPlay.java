package javaphone;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс для управления аудио в видео звонках
 * Поддерживает многопользовательское аудио и воспроизведение системных звуков
 */
public class AudioPlay {
    // Формат аудио для воспроизведения
    private final AudioFormat playbackFormat;

    // Карта активных участников звонка (ID -> аудиолиния)
    private final Map<String, SourceDataLine> activeSpeakers;

    // Пул потоков для асинхронного воспроизведения
    private final ExecutorService audioExecutor;

    // Флаг работы аудиосистемы
    private volatile boolean isRunning;

    // Флаг воспроизведения WAV-файла
    private volatile boolean isPlaying;

    // Аудиолиния для системных звуков (звонков, уведомлений)
    private SourceDataLine speakers;

    /**
     * Конструктор
     * @param format аудиоформат для воспроизведения
     */
    public AudioPlay(AudioFormat format) {
        this.playbackFormat = format;
        this.activeSpeakers = new HashMap<>();
        this.audioExecutor = Executors.newCachedThreadPool();
        this.isRunning = true;
    }

    /**
     * Добавить участника звонка
     * @param participantId уникальный ID участника
     */
    public void addParticipant(String participantId) throws LineUnavailableException {
        if (!activeSpeakers.containsKey(participantId)) {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, playbackFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(playbackFormat);
            line.start();
            activeSpeakers.put(participantId, line);
        }
    }

    /**
     * Воспроизвести аудиоданные для конкретного участника
     * @param participantId ID участника
     * @param pcmData сырые аудиоданные в PCM формате
     */
    public void playAudioForParticipant(String participantId, byte[] pcmData) {
        if (!isRunning || !activeSpeakers.containsKey(participantId)) {
            return;
        }

        audioExecutor.submit(() -> {
            SourceDataLine line = activeSpeakers.get(participantId);
            if (line != null && line.isOpen()) {
                line.write(pcmData, 0, pcmData.length);
            }
        });
    }

    /**
     * Воспроизвести WAV-файл (для звонков, уведомлений)
     * @param filename путь к WAV-файлу
     */
    public void playWav(String filename) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        isPlaying = true;
        AudioInputStream in = AudioSystem.getAudioInputStream(new File(filename));
        AudioFormat baseFormat = in.getFormat();

        // Конвертируем в нужный формат
        AudioFormat decodedFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            baseFormat.getSampleRate(),
            16,
            baseFormat.getChannels(),
            baseFormat.getChannels() * 2,
            baseFormat.getSampleRate(),
            false
        );

        AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
        speakers = AudioSystem.getSourceDataLine(decodedFormat);
        speakers.open(decodedFormat);
        speakers.start();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while (isPlaying && (bytesRead = din.read(buffer)) != -1) {
            speakers.write(buffer, 0, bytesRead);
        }

        speakers.drain();
        speakers.stop();
        speakers.close();
        din.close();
        in.close();
    }

    /**
     * Остановить воспроизведение WAV-файла
     */
    public void stopWav() {
        isPlaying = false;
        if (speakers != null) {
            speakers.stop();
            speakers.close();
            speakers = null;
        }
    }

    /**
     * Удалить участника звонка
     * @param participantId ID участника
     */
    public void removeParticipant(String participantId) {
        SourceDataLine line = activeSpeakers.remove(participantId);
        if (line != null) {
            line.stop();
            line.close();
        }
    }

    /**
     * Полная остановка аудиосистемы
     */
    public void stop() {
        isRunning = false;
        audioExecutor.shutdownNow();
        activeSpeakers.values().forEach(line -> {
            line.stop();
            line.close();
        });
        activeSpeakers.clear();
    }

    /**
     * Установить громкость для участника
     * @param participantId ID участника
     * @param volume уровень громкости (0.0 - 1.0)
     */
    public void setParticipantVolume(String participantId, float volume) {
        SourceDataLine line = activeSpeakers.get(participantId);
        if (line != null && line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
}