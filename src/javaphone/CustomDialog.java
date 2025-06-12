package javaphone;


import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

class CustomDialog extends JDialog {
    private JTextField ipInput;
    private String action;

    public CustomDialog(JFrame parent) {
        super(parent, "Ввод данных", true); // Модальное окно
        setLayout(new GridLayout(3, 2, 5, 5));
        setSize(300, 150);
        setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        JLabel label1 = new JLabel("IP:");
        ipInput = new JTextField();
        JButton dmButton = new JButton("Write");
        JButton pingButton = new JButton("Ping");


        dmButton.addActionListener(e -> decision(CallCodes.callDM));
        dmButton.addActionListener(e -> decision(CallCodes.callPing));

        add(label1);
        add(ipInput);
        add(new JLabel());
        add(dmButton);
        add(pingButton);
    }

    @Override
    public void dispose() {
        ipInput.setText("");
        super.dispose();
    }

    private void decision(String action){
        this.action = action;
        if(ipInput.getText().equals("")){
            this.dispose();
        } else {
            super.dispose();
        }
    }

    public String getField1Value() {
        return ipInput.getText();
    }
    
    public String getActionValue() {
        return action;
    }
}