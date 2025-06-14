/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javaphone.EventInterfaces.DMHandler;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author bafc13
 */
public class ChatArea extends javax.swing.JPanel implements DMHandler {

    private JPanel chatPanel;
    private JScrollPane chatPane;
    private JTextField inputField;
    public JTextArea userArea;
    private JScrollPane userPane;
    private DragDropEditorPane editorPane;
    private DirectMessenger dm;

    public ChatArea(Dimension screenSize, boolean isCall, DirectMessenger dm) {
        super();
        this.dm = dm;
        if (isCall == false) {
            this.setSize(screenSize.width / 2, screenSize.height / 2);
        } else {
            if (chatPanel != null) {
                this.remove(chatPanel);
                this.remove(userPane);
            }
            this.setSize(screenSize.width / 2, screenSize.height / 3 - 100);
        }

        initChatPanel(isCall, screenSize);
        initChatPane(isCall, screenSize);
        inputField = new JTextField();
        inputField.setFont(new Font("Arial Unicode MS", Font.PLAIN, 18));
        inputField.addActionListener(e -> {
            try {
                sendMessageText();
            } catch (BadLocationException ex) {
                Logger.getLogger(ChatArea.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ChatArea.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        chatPanel.add(chatPane, BorderLayout.CENTER);
        chatPanel.add(inputField);

        initUserPane(isCall, screenSize);

        dm.addListener(this);
        dm.addListener(MainWindow.db);
        this.add(chatPanel);
        this.add(userPane);

        loadHistory();
    }

    private void initChatPanel(boolean isCall, Dimension screenSize) {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        if (isCall == false) {
            chatPanel.setMinimumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 + 100));
            chatPanel.setPreferredSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 + 100));
            chatPanel.setMaximumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 + 100));
        } else {
            chatPanel.setMinimumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 - 100));
            chatPanel.setPreferredSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 - 100));
            chatPanel.setMaximumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 - 100));
        }
        chatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void initChatPane(boolean isCall, Dimension screenSize) {
        editorPane = new DragDropEditorPane(dm);
        addHyperlinkListener();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setFont(new Font("Arial Unicode MS", Font.BOLD, 16));

        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body { word-wrap: wrap }");
        editorPane.setEditorKit(kit);
        chatPane = new JScrollPane(editorPane);
        chatPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatPane.setBorder(new RoundedBorder(5));
        chatPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (isCall == false) {
            chatPane.setMinimumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 + 70));
            chatPane.setPreferredSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 + 70));
            chatPane.setMaximumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 + 70));
        } else {
            chatPane.setMinimumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 - 130));
            chatPane.setPreferredSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 - 130));
            chatPane.setMaximumSize(new Dimension(screenSize.width / 4 + 100, screenSize.height / 3 - 130));
        }
    }

    private void initUserPane(boolean isCall, Dimension screenSize) {
        userArea = new JTextArea("");
        userArea.setEditable(false);
        userArea.setFont(new Font("Arial Unicode MS", Font.BOLD, 20));
        userArea.setLineWrap(true);
        userArea.setWrapStyleWord(true);
        userPane = new JScrollPane(userArea);
        userPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        if (isCall == false) {
            userPane.setMinimumSize(new Dimension(screenSize.width / 4 - 100, screenSize.height / 3 + 100));
            userPane.setPreferredSize(new Dimension(screenSize.width / 4 - 100, screenSize.height / 3 + 100));
            userPane.setMaximumSize(new Dimension(screenSize.width / 4 - 100, screenSize.height / 3 + 100));
        } else {
            userPane.setMinimumSize(new Dimension(screenSize.width / 4 - 100, screenSize.height / 3 - 100));
            userPane.setPreferredSize(new Dimension(screenSize.width / 4 - 100, screenSize.height / 3 - 100));
            userPane.setMaximumSize(new Dimension(screenSize.width / 4 - 100, screenSize.height / 3 - 100));
        }
        userArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        userPane.setBorder(new RoundedBorder(5));
    }

    private void addHyperlinkListener() {
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    URI uri = e.getURL().toURI();

                    if ("file".equals(uri.getScheme())) {
                        File file = new File(uri);

                        if (file.exists()) {
                            if (file.isDirectory()) {
                                // Открыть папку в проводнике
                                Desktop.getDesktop().open(file);
                            } else {
                                // Открыть файл в ассоциированной программе
                                Desktop.getDesktop().open(file);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Файл не найден: " + file.getPath(),
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void loadHistory() {
        List<HashMap<String, String>> history = MainWindow.db.getChatHistory(dm.chatID);
        for (HashMap<String, String> msg : history) {
            addMessage(msg.get("name"), msg.get("message"));
        }
    }

    private void addMessage(String username, String content) {

        HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
        try {
            doc.insertAfterEnd(
                    doc.getCharacterElement(doc.getLength()),
                    username + ": " + content + "<br>"
            );
        } catch (BadLocationException ex) {
            Logger.getLogger(ChatArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void HandleDMText(int chatID, String address, String text) {
        if (dm.chatID != chatID) {
            return;
        }

        String username = MainWindow.db.getUsername(address);
        addMessage(username, text);
    }

    @Override
    public void HandleDMFile(int chatID, String address, String fname) {
        if (dm.chatID != chatID) {
            return;
        }

        String username = MainWindow.db.getUsername(address);
        editorPane.addFileToDocument(fname);
    }

    private void sendMessageText() throws BadLocationException, IOException {
        if (!"".equals(inputField.getText())) {
            try {
                dm.sendText(inputField.getText());
                inputField.setText("");
            } catch (IOException ex) {
                Logger.getLogger(CallFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

//    private void sendFile()
}
