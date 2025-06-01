package javaphone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import javaphone.EventInterfaces.SubtitleHandler;

/**
 * Класс для логирования сообщений пользователей в файл.
 * Реализует интерфейс SubtitleHandler для обработки субтитров/сообщений.
 * Формат записи: "username: message"
 */
public class WriterToFile implements SubtitleHandler {
    
    // Менеджер базы данных для получения информации о пользователях
    private final DBManager dbManager;
    
    // Путь к файлу лога
    private final String logFilePath;
    
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
    public WriterToFile(DBManager dbManager, String logFilePath) {
        this.dbManager = dbManager;
        this.logFilePath = logFilePath;
    }
    
    public void SubtitleLineRecorded(String line){}


    /**
     * Обработчик полученных сообщений с информацией о пользователе.
     * Формирует строку лога в формате "username: message" и записывает в файл.
     * 
     * @param chatID идентификатор чата (не используется в текущей реализации)
     * @param ipAddress IP-адрес отправителя (для идентификации пользователя)
     * @param message текст сообщения
     */
    @Override
    public void SubtitleLineReceived(int chatID, String ipAddress, String message) {
        // Получаем имя пользователя по IP-адресу
        String username = dbManager.getUsername(ipAddress);
        
        // Форматируем строку для записи в лог
        String logLine = String.format("%s: %s", username, message);
        
     
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
            System.err.println("Ошибка записи в лог: " + e.getMessage());
            
        }
    }
}