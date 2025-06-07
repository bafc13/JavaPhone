/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package javaphone.EventInterfaces;

/**
 *
 * @author Andrey
 */
public interface NotificationHandler {
    public void messageReceived(int chatID, String senderIP, String content, Boolean isFile);
    public Boolean callReceived(int chatID, String senderIP);
}
