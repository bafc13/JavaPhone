package javaphone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IncomingCallPanel extends JPanel {
    private JLabel label;
    private JButton acceptButton;
    private JButton declineButton;
    public int accept = 0;
    public int decline = 0;

    public IncomingCallPanel() {
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.setPreferredSize(new Dimension(1920, 100));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        label = new JLabel("Вам звонят", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));

        acceptButton = new JButton("Принять звонок");
        declineButton = new JButton("Отклонить звонок");
        panel.add(acceptButton);
        panel.add(declineButton);

        this.add(panel, BorderLayout.CENTER);
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accept = 1;
            }
        });

        declineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                decline = 1;
            }
        });

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // Для тестирования виджета отдельно
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Входящий звонок");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new IncomingCallPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}