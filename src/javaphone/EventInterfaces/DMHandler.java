/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone.EventInterfaces;

/**
 *
 * @author Andrey
 */
public interface DMHandler {
    void HandleDMText(int chatID, String senderIP, String text);
    void HandleDMFile(int chatID, String senderIP, String fname);
}
