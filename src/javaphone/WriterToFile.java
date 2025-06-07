package javaphone;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.SubtitleHandler;

/**
 * Класс для логирования сообщений пользователей в файл.
 * Реализует интерфейс SubtitleHandler для обработки субтитров/сообщений.
 * Формат записи: "username: message"
 */
public class WriterToFile implements SubtitleHandler {
    // Путь к файлу лога
    private final String logFilePath;
    private final int chatID;
    
    // Форматтер даты и времени (не используется в текущей реализации,
    // но может быть полезен для расширения функциональности)
    //private static final DateTimeFormatter dtf = 
        //DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Конструктор класса WriterToFile.
     * 
     * @param dbManager экземпляр DBManager для получения имен пользователей
     * @param logFilePath путь к файлу, в который будет записываться лог
     */
    public WriterToFile(int chatID, String logFilePath) {
        this.chatID = chatID;
        this.logFilePath = logFilePath;
        
        try {
            PrintWriter writer = new PrintWriter(logFilePath, "UTF-8");
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WriterToFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(WriterToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void SubtitleLineRecorded(String line){
        // Получаем имя пользователя по IP-адресу
        String username = MainWindow.db.getUsername("localhost");
        
        // Форматируем строку для записи в лог
        String logLine = String.format("%s: %s", username, line);
        
        Write(logLine);
    }


    /**
     * Обработчик полученных сообщений с информацией о пользователе.
     * Формирует строку лога в формате "username: message" и записывает в файл.
     * 
     * @param chatID идентификатор чата (не используется в текущей реализации)
     * @param ipAddress IP-адрес отправителя (для идентификации пользователя)
     * @param line текст сообщения
     */
    @Override
    public void SubtitleLineReceived(int chatID, String ipAddress, String line) {
        if (this.chatID != chatID)
            return;
        
        // Получаем имя пользователя по IP-адресу
        String username = MainWindow.db.getUsername(ipAddress);
        
        // Форматируем строку для записи в лог
        String logLine = String.format("%s: %s", username, line);
        
        Write(logLine);
    }
    
    public void Write(String logLine)
    {
        try (
            // Создаем цепочку писателей:
            // PrintWriter -> BufferedWriter -> FileWriter
            PrintWriter out = new PrintWriter(
                new BufferedWriter(
                    new FileWriter(logFilePath, true) // true - режим дозаписи
                )
            )
        ) {
            // Записываем сформированную строку в файл
            out.println(logLine);
            
            // Примечание:
            // 1. BufferedWriter обеспечивает буферизацию для эффективной записи
            // 2. FileWriter открывает файл в режиме дозаписи (append)
            // 3. Все ресурсы автоматически закроются при выходе из блока try
        } catch (IOException e) {
            // Обработка ошибок ввода-вывода
            Logger.getLogger(WriterToFile.class.getName()).log(Level.SEVERE, null, e);
            
        }
    }
}