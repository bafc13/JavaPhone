/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrey
 */
public class DBManager implements JavaPhoneEvents {

    private Connection c;

    String sql_check_user_exists = "SELECT 1 FROM users WHERE ip = ?";

    String sql_update_username = "UPDATE users SET name = ? WHERE ip = ?";
    String sql_add_user = "INSERT INTO users(ip, name) VALUES (?, ?)";
    String sql_add_dm = "INSERT INTO chats(type, host_ip) VALUES (\"dm\", \"\")";

    String sql_find_dm_exists = "SELECT chats.id FROM chats JOIN chats_users ON chats.id = chats_users.chat_id WHERE chats.type = \"dm\" AND chats_users.user_ip = ?";
    String sql_find_dm_created = "SELECT id FROM chats ORDER BY id DESC LIMIT 1";

    String sql_add_user_to_dm = "INSERT INTO chats_users(chat_id, user_ip) VALUES (?, ?)";
    String sql_add_yourself_to_dm = "INSERT INTO chats_users(chat_id, user_ip) VALUES (?, \"localhost\")";
    
    String sql_add_dm_message = "INSERT INTO messages(chat_id, sender_ip, content, time) VALUES (?, ?, ?, ?)";
    String sql_add_file = "INSERT INTO media(path) VALUES (?)";
    String sql_add_file_to_message = "INSERT INTO attachments(message_id, media_id) VALUES (?, ?)";
    
    String sql_find_last_message = "SELECT id FROM messages ORDER BY id DESC LIMIT 1";
    String sql_find_last_file = "SELECT id FROM media ORDER BY id DESC LIMIT 1";

    public DBManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:JP.sqlite");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        // System.out.println("Opened database successfully");
    }

    @Override
    public void callRecieved(Handshake hs) {
        String ip = hs.sock.getInetAddress().toString();

        try {
            PreparedStatement stmt_cue = c.prepareStatement(sql_check_user_exists);
            stmt_cue.setString(1, ip);
            ResultSet rs_cue = stmt_cue.executeQuery();

            if (!rs_cue.isBeforeFirst()) {
                PreparedStatement stmt_au = c.prepareStatement(sql_add_user);
                stmt_au.setString(1, ip);
                stmt_au.setString(2, hs.name);
                stmt_au.executeUpdate();
                
                PreparedStatement stmt_ad = c.prepareStatement(sql_add_dm);
                stmt_ad.executeUpdate();
                
                PreparedStatement stmt_fdc = c.prepareStatement(sql_find_dm_created);
                ResultSet rs_fdc = stmt_fdc.executeQuery();
                
                int chat_id = rs_fdc.getInt("id");
                
                PreparedStatement stmt_aud = c.prepareStatement(sql_add_user_to_dm);
                stmt_aud.setInt(1, chat_id);
                stmt_aud.setString(2, ip);
                stmt_aud.executeUpdate();
                
                PreparedStatement stmt_ayd = c.prepareStatement(sql_add_yourself_to_dm);
                stmt_ayd.setInt(1, chat_id);
                stmt_ayd.executeUpdate();
            } else {
                PreparedStatement stmt_uun = c.prepareStatement(sql_update_username);
                stmt_uun.setString(1, ip);
                stmt_uun.executeUpdate();
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void handleDM_text(String dm_address, String address, String text) {
        try {
            int dm_id = get_dm_id(dm_address);
            
            PreparedStatement stmt_adm = c.prepareStatement(sql_add_dm_message);
            
            stmt_adm.setInt(1, dm_id);
            stmt_adm.setString(2, address);
            stmt_adm.setString(3, text);
            stmt_adm.setLong(4, System.currentTimeMillis() / 1000L);
            
            stmt_adm.executeUpdate();
        } catch (SQLException ex) {            
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void handleDM_file(String dm_address, String address, String fname) {
        try {
            int dm_id = get_dm_id(dm_address);
            
            PreparedStatement stmt_adm = c.prepareStatement(sql_add_dm_message);
            
            stmt_adm.setInt(1, dm_id);
            stmt_adm.setString(2, address);
            stmt_adm.setString(3, fname);
            stmt_adm.setLong(4, System.currentTimeMillis() / 1000L);
            
            stmt_adm.executeUpdate();
            
            int message_id = c.prepareStatement(sql_find_last_message).executeQuery().getInt("id");
            
            PreparedStatement stmt_af = c.prepareStatement(sql_add_file);
            stmt_af.setString(1, fname);
            
            int file_id = c.prepareStatement(sql_find_last_file).executeQuery().getInt("id");
            
            PreparedStatement stmt_afm = c.prepareStatement(sql_add_file_to_message);
            stmt_afm.setInt(1, message_id);
            stmt_afm.setInt(2, file_id);
            stmt_afm.executeUpdate();
            
        } catch (SQLException ex) {            
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int get_dm_id(String ip)
    {
        try {
            PreparedStatement stmt = c.prepareStatement(sql_find_dm_exists);
            stmt.setString(1, ip);
            ResultSet rs = stmt.executeQuery();
            
            return rs.getInt(1);
        } catch (SQLException ex) {            
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            
            return -1;
        }
    }
}
