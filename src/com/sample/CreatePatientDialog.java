package com.sample;

import com.sample.data.PatientController;
import org.sqlite.SQLiteException;

import javax.swing.*;
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
        // update database table
        HashMap<String, Object> data = this.getData();
        try {
            // update patient in database
            new PatientController().add(data);

            result = true;
            dispose();

        } catch (SQLiteException ex) {
            if (ex.getResultCode().code == 1555) {    // SQLITE_CONSTRAINT_PRIMARYKEY
                JOptionPane.showMessageDialog(this, "The ID has been used. Please use another ID");
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
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("id", txtId.getText());
        data.put("name", txtName.getText());
        data.put("surname", txtSurname.getText());
        data.put("phone", txtPhone.getText());
        data.put("email", txtEmail.getText());
        data.put("medical_conditions", txtMedicalConditions.getText());

        return data;
    }
}
