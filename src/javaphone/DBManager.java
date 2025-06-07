/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.*;

/**
 *
 * @author Andrey
 */
public class DBManager implements CallHandler, DMHandler {

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
    String sql_get_user = "SELECT name FROM users WHERE ip = ?";

    String sql_find_file_checksum = "SELECT id, path FROM media WHERE checksum = ?";
    String sql_get_chat_history = "SELECT name, content FROM messages JOIN users ON users.ip = messages.sender_ip WHERE chat_id = ? ORDER BY time";

    String sql_get_users = "SELECT ip, name FROM users";

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
        String ip = hs.sock.getInetAddress().toString().substring(1);

        try {
            PreparedStatement stmt_cue = c.prepareStatement(sql_check_user_exists);
            stmt_cue.setString(1, ip);
            ResultSet rs_cue = stmt_cue.executeQuery();

            if (setUsername(ip, hs.name)) {
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
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void HandleDMText(int chatID, String address, String text) {
        try {
            PreparedStatement stmt_adm = c.prepareStatement(sql_add_dm_message);

            stmt_adm.setInt(1, chatID);
            stmt_adm.setString(2, address);
            stmt_adm.setString(3, text);
            stmt_adm.setLong(4, System.currentTimeMillis() / 1000L);

            stmt_adm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void HandleDMFile(int chatID, String address, String fname) {
        try {
            PreparedStatement stmt_adm = c.prepareStatement(sql_add_dm_message);

            stmt_adm.setInt(1, chatID);
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

    public int getDmId(String ip) {
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

    public Boolean setUsername(String ip, String newUsername) {
        try {
            PreparedStatement stmt_cue = c.prepareStatement(sql_check_user_exists);
            stmt_cue.setString(1, ip);
            ResultSet rs_cue = stmt_cue.executeQuery();

            if (!rs_cue.isBeforeFirst()) {
                PreparedStatement stmt_au = c.prepareStatement(sql_add_user);
                stmt_au.setString(1, ip);
                stmt_au.setString(2, newUsername);
                stmt_au.executeUpdate();
                return true;
            } else {
                PreparedStatement stmt_uun = c.prepareStatement(sql_update_username);
                stmt_uun.setString(1, newUsername);
                stmt_uun.setString(2, ip);
                stmt_uun.executeUpdate();
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String getUsername(String ip) {
        try {
            PreparedStatement stmt = c.prepareStatement(sql_get_user);
            stmt.setString(1, ip);
            ResultSet rs = stmt.executeQuery();

            return rs.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);

            return "Unknown";
        }
    }

    public String countChecksum(String path) {
        byte[] buffer = new byte[8192];
        int count;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path))) {
            while ((count = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } catch (IOException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

        byte[] hash = digest.digest();
        return new String(hash);
    }

    public void copyFile(String path) {
        File source = new File(path);
        File copy = new File("./files/" + source.getName());
        try (InputStream in = new BufferedInputStream(new FileInputStream(source));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(copy))) {

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String findFileWithChecksum(String checksum)
    {
        try {
            PreparedStatement stmt = c.prepareStatement(sql_find_file_checksum);
            stmt.setString(1, checksum);
            ResultSet rs = stmt.executeQuery();
            if (rs.isBeforeFirst()) {
                return rs.getString(2);
            } else {
                return "";
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public List getChatHistory(int chatID) {
        try {
            PreparedStatement stmt = c.prepareStatement(sql_get_chat_history);
            ResultSet rs = stmt.executeQuery();

            List<HashMap<String, String>> result = new ArrayList<>();
            int cur = 0;

            while (rs.next()) {
                result.add(new HashMap<>());
                result.get(cur).put("name", rs.getString(1));
                result.get(cur).put("message", rs.getString(2));
                cur++;
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<>();
    }
    
    public List getFriends() {
        try {
            PreparedStatement stmt = c.prepareStatement(sql_get_users);
            ResultSet rs = stmt.executeQuery();

            List<HashMap<String, String>> result = new ArrayList<>();
            int cur = 0;

            while (rs.next()) {
                result.add(new HashMap<>());
                result.get(cur).put("ip", rs.getString(1));
                result.get(cur).put("name", rs.getString(2));
                cur++;
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ArrayList<>();
    }

    public void addFile(String path) {
        String hash = countChecksum(path);

        try {
            PreparedStatement stmt = c.prepareStatement(sql_find_file_checksum);
            stmt.setString(1, hash);
            ResultSet rs = stmt.executeQuery();
            if (rs.isBeforeFirst()) {
                copyFile(path);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void callSent(Handshake hs) {
        callRecieved(hs);
    }

    @Override
    public void callFailed(String ip, String purpose) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
