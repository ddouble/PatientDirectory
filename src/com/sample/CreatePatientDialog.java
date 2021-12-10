package com.sample;

import com.sample.data.PatientController;
import org.sqlite.SQLiteException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.HashMap;

public class CreatePatientDialog extends JDialog {
    private JPanel contentPane;
    private JButton btnOk;
    private JButton btnCancel;
    private JTextField txtId;
    private JTextField txtSurname;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtMedicalConditions;
    private boolean result = false;


    public CreatePatientDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnOk);

        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void onOK() {
        HashMap<String, Object> data = this.getData();
        Color errorTxtBgColor = new Color(240,200,220);
        Color defaultTxtBgColor = new JTextField().getBackground();

        // validate input
        if (!InputValidator.isPatientId(data.get("id").toString())) {
            txtId.setBackground(errorTxtBgColor);
            JOptionPane.showMessageDialog(this, "id can only be digits and letters.");
            return;
        }
        txtId.setBackground(defaultTxtBgColor);


        if (!InputValidator.isName(data.get("name").toString())) {
            txtName.setBackground(errorTxtBgColor);
            JOptionPane.showMessageDialog(this, "name can only contain letters and space.");
            return;
        }
        txtName.setBackground(defaultTxtBgColor);

        if (!InputValidator.isName(data.get("surname").toString())) {
            txtSurname.setBackground(errorTxtBgColor);
            JOptionPane.showMessageDialog(this, "surname can only contain letters and space.");
            return;
        }
        txtSurname.setBackground(defaultTxtBgColor);

        if (!InputValidator.isPhone(data.get("phone").toString())) {
            txtPhone.setBackground(errorTxtBgColor);
            JOptionPane.showMessageDialog(this, "phone must be not less than 5 digits.");
            return;
        }
        txtPhone.setBackground(defaultTxtBgColor);

        if (!InputValidator.isEmail(data.get("email").toString())) {
            txtEmail.setBackground(errorTxtBgColor);
            JOptionPane.showMessageDialog(this, "email address format  must be valid.");
            return;
        }
        txtEmail.setBackground(defaultTxtBgColor);

        if (!InputValidator.isMedicalConditionsString(data.get("medical_conditions").toString())) {
            txtMedicalConditions.setBackground(errorTxtBgColor);
            JOptionPane.showMessageDialog(this, "medical conditions must be this form: \nsneezes,headache");
            return;
        }
        txtMedicalConditions.setBackground(defaultTxtBgColor);

        // update patient in database
        try {
            new PatientController().add(data);

            result = true;
            dispose();

        } catch (SQLiteException ex) {
            if (ex.getResultCode().code == 1555) {    // SQLITE_CONSTRAINT_PRIMARYKEY
                JOptionPane.showMessageDialog(this, "The ID has been used. Please use another ID");

                txtId.setBackground(errorTxtBgColor);
            } else ex.printStackTrace();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void onCancel() {
        // add your code here if necessary
        result = false;
        dispose();
    }

    public boolean isOk() {
        return result;
    }

    public HashMap<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", txtId.getText().trim());
        data.put("name", txtName.getText().trim());
        data.put("surname", txtSurname.getText().trim());
        data.put("phone", txtPhone.getText().trim());
        data.put("email", txtEmail.getText().trim());
        data.put("medical_conditions", txtMedicalConditions.getText().trim());

        return data;
    }
}
