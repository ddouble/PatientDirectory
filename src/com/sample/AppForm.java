package com.sample;

import com.sample.data.Connection;
import com.sample.data.Patient;
import com.sample.data.PatientController;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class AppForm {
    private final DefaultTableModel patientsTableModel;
    private JButton btnNewPatient;
    private JPanel pnlAppForm;
    private JTextField txtSearchKey;
    private JTable tblPatients;
    private JButton btnSearch;

    private final String[] patientsTableHeaderNames = new String[]{"ID", "Name", "Surname", "Phone Number.", "Email", "Medical Conditions"};
    private final String[] patientsTableFieldNames = new String[]{"id", "name", "surname", "phone", "email", "medical_conditions"};

    public AppForm() throws SQLException {
        initNewPatientButton();

        patientsTableModel = initPatientsTable();

        initSearchButton();
    }

    /**
     * initialize search button
     */
    private void initSearchButton() {
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String key = txtSearchKey.getText().trim();

                try {
                    ArrayList<Patient> patients = key.isBlank() ? new PatientController().all() : new PatientController().search(key);
                    int n = patientsTableModel.getRowCount();
                    for (int i = 0; i < n; i++) {
                        patientsTableModel.removeRow(0);
                    }
                    for (Patient p : patients) {
                        patientsTableModel.addRow(new Object[]{
                                p.id,
                                p.name,
                                p.email,
                                p.phone,
                                p.email,
                                String.join(",", p.getMedicalConditions()),
                        });
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        };

        btnSearch.addActionListener(listener);
        txtSearchKey.addActionListener(listener);
    }

    /**
     * Initialize patients table
     *
     * @return
     * @throws SQLException
     */
    private DefaultTableModel initPatientsTable() throws SQLException {
        final DefaultTableModel patientsTableModel;
        // initialize patients table
        patientsTableModel = new DefaultTableModel(patientsTableHeaderNames, 0);

        patientsTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int lastRow = e.getLastRow();
                int column = e.getColumn();
                DefaultTableModel model = (DefaultTableModel) e.getSource();

                if (column > -1 && tblPatients.isEditing()) {
                    String columnName = model.getColumnName(column);
                    Object id = model.getValueAt(row, 0);
                    Object v = model.getValueAt(row, column);

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("id", id);
                    data.put(patientsTableFieldNames[column], v);
                    try {
                        new PatientController().update(data);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        try {
            ArrayList<Patient> patients = new PatientController().all();
            for (Patient p : patients) {
                patientsTableModel.addRow(new Object[]{
                        p.id,
                        p.name,
                        p.email,
                        p.phone,
                        p.email,
                        String.join(",", p.getMedicalConditions()),
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        tblPatients.getTableHeader().setReorderingAllowed(false);
        tblPatients.setModel(patientsTableModel);

        return patientsTableModel;
    }

    /**
     * Initlize new patient button
     */
    private void initNewPatientButton() {
        btnNewPatient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Hello World.");

                CreatePatientDialog dialog = new CreatePatientDialog();
//                dialog.pack();
                dialog.setSize(600, 300);
                dialog.setLocationRelativeTo(pnlAppForm);
                dialog.setVisible(true);
                if (dialog.isOk()) {
                    HashMap<String, Object> data = dialog.getData();

                    // update ui
                    patientsTableModel.addRow(new Object[]{
                            data.get("id"),
                            data.get("name"),
                            data.get("surname"),
                            data.get("phone"),
                            data.get("email"),
                            data.get("medical_conditions")});
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame("Patient Directory");
            frame.setContentPane(new AppForm().pnlAppForm);
            frame.setSize(800, 500);
//        frame.pack();
            frame.setVisible(true);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);

                    // release resources
                    try {
                        Connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
