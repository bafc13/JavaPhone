/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaphone;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author bafc13
 */
public class NotificationDialog extends JDialog {
    private String callCode;
    private String nickname;
    private boolean response = false;

    public NotificationDialog(JFrame parent, String callCode, String nickname) {
        super(parent, "Уведомление", false);
        this.nickname = nickname;
        this.callCode = callCode;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogDimension = new Dimension(300, 150);


        setLayout(new BorderLayout());
        setSize(dialogDimension);
        setLocation(screenSize.width - dialogDimension.width, screenSize.height - dialogDimension.height);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        JButton acceptButton = new JButton();
        acceptButton.addActionListener(e -> {
            response = true;
            this.dispose();
        });

        JButton rejectButton = new JButton();
        rejectButton.addActionListener(e -> {
            response = false;
            this.dispose();
        });
        buttonPanel.add(rejectButton);
        buttonPanel.add(acceptButton);

        String reasonToNotify = "";
        switch (callCode) {
            case CallCodes.callDM:
                reasonToNotify = " пишет вам";
                acceptButton.setText("пообщаться");
                rejectButton.setText("отклонить общение");
                break;
            case CallCodes.callVoiceVideo:
                reasonToNotify = " звонит вам";
                acceptButton.setText("ответить");
                rejectButton.setText("отклонить вызов");
                break;
            default:
                break;
        }
        JLabel notificationText = new JLabel("Пользователь " + nickname + reasonToNotify);


        this.add(notificationText, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public boolean getResponse() {
        return response;
    }
}
