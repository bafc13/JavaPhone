package javaphone;


import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

class CustomDialog extends JDialog {
    private JTextField field1;
    private JTextField field2;

    public CustomDialog(JFrame parent) {
        super(parent, "Ввод данных", true); // Модальное окно
        setLayout(new GridLayout(3, 2, 5, 5));
        setSize(300, 150);
        setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel label1 = new JLabel("IP:");
        JLabel label2 = new JLabel("NICKNAME:");
        field1 = new JTextField();
        field2 = new JTextField();
        JButton okButton = new JButton("OK");


        okButton.addActionListener(e -> decision());

        add(label1);
        add(field1);
        add(label2);
        add(field2);
        add(new JLabel());
        add(okButton);
    }

    @Override
    public void dispose() {
        field1.setText("CLOSE");
        field2.setText("OPER");
        super.dispose();
    }

    private void decision(){
        if(field1.getText().equals("") && field2.getText().equals("")){
            this.dispose();
        } else if (field1.getText().equals("") || field2.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Введите второе поле!");
        }
    }

    public String getField1Value() {
        return field1.getText();
    }

    public String getField2Value() {
        return field2.getText();
    }
}