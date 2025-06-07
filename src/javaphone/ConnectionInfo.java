/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

/**
 *
 * @author Andrey
 */
public class ConnectionInfo {
    public int chatID;
    public DirectMessenger dm;
    public Boolean hasWindow;
    public Boolean toOpen;
    
    public ConnectionInfo() {
        chatID = -1;
        dm = null;
        hasWindow = false;
        toOpen = false;
    }
    
    public ConnectionInfo(DirectMessenger dm) {
        this.dm = dm;
        chatID = dm.chatID;
        
        hasWindow = false;
        toOpen = false;
    }
    
    public ConnectionInfo(DirectMessenger dm, Boolean toOpen) {
        this.dm = dm;
        chatID = dm.chatID;
        
        hasWindow = false;
        this.toOpen = toOpen;
    }
    
    public ConnectionInfo(Boolean toOpen) {
        chatID = -1;
        dm = null;
        hasWindow = false;
        this.toOpen = toOpen;
    }
}
