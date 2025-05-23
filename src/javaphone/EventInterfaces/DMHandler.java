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
    void HandleDMText(String dm_address, String address, String text);
    void HandleDMFile(String dm_address, String address, String fname);
}
