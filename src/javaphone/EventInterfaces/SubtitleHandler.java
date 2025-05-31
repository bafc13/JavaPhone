/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javaphone.EventInterfaces;

/**
 *
 * @author Andrey
 */
public interface SubtitleHandler {
    public void SubtitleLineRecorded(String line);
    public void SubtitleLineReceived(int chatID, String address, String line);
}
