package com.sample.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class PatientController {

    /**
     * Get all patients from database
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<Patient> all() throws SQLException {
        Statement stmt = Connection.get().createStatement();

        stmt.setQueryTimeout(30);  // set timeout to 30 sec.
        ResultSet rs = stmt.executeQuery("SELECT * FROM patients ORDER BY id");

        ArrayList<Patient> rows = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString("id");
            Patient patient = new Patient(id, rs.getString("name"), rs.getString("surname"), rs.getString("phone"), rs.getString("email"));

            patient.setMedicalConditions(this.medicalConditions(id));

            rows.add(patient);
        }
        rs.close();
        stmt.close();

        return rows;

    }

    /**
     * Get all medical conditions of the specific patient identified by patientId from database
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<String> medicalConditions(String patientId) throws SQLException {
        PreparedStatement stmt = Connection.get().prepareStatement("SELECT * FROM patient_medical_conditions WHERE patient_id=?");
        stmt.setString(1, patientId);
        ResultSet rs = stmt.executeQuery();

        ArrayList<String> rows = new ArrayList<>();
        while (rs.next()) {
            String mc = rs.getString("medical_condition");
            rows.add(mc);
        }
        rs.close();
        stmt.close();

        return rows;
    }

    /**
     * Search patients by key from database
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<Patient> search(String key) throws SQLException {
        PreparedStatement stmt = Connection.get().prepareStatement(
                "SELECT DISTINCT p.* "
                        + "FROM patients p INNER JOIN patient_medical_conditions pmc ON p.id=pmc.patient_id "
                        + "WHERE p.id LIKE ? OR p.name LIKE ? OR p.surname LIKE ? OR p.phone LIKE ? OR p.email LIKE ? OR pmc.medical_condition LIKE ?"
                        + "ORDER BY p.id");

        stmt.setString(1, "%"+key+"%");
        stmt.setString(2, "%"+key+"%");
        stmt.setString(3, "%"+key+"%");
        stmt.setString(4, "%"+key+"%");
        stmt.setString(5, "%"+key+"%");
        stmt.setString(6, "%"+key+"%");
        ResultSet rs = stmt.executeQuery();

        ArrayList<Patient> rows = new ArrayList<>();
        ArrayList<String> medicalConditions = new ArrayList<>();
        Patient patient = null;
        while (rs.next()) {
            String id = rs.getString("id");
            patient = new Patient(id, rs.getString("name"), rs.getString("surname"), rs.getString("phone"), rs.getString("email"));

            patient.setMedicalConditions(this.medicalConditions(id));
            rows.add(patient);
        }
        rs.close();
        stmt.close();

        return rows;
    }

    /**
     * insert a new patient to database
     *
     * @param data
     * @return
     * @throws SQLException
     */
    public boolean add(HashMap<String, Object> data) throws SQLException {
        try {
            Connection.get().setAutoCommit(false);
            String sql = "INSERT INTO patients (id, name, surname, phone, email) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement stmt = Connection.get().prepareStatement(sql);
            String id = data.get("id").toString();
            stmt.setString(1, id);
            stmt.setString(2, data.get("name").toString());
            stmt.setString(3, data.get("surname").toString());
            stmt.setString(4, data.get("phone").toString());
            stmt.setString(5, data.get("email").toString());
            stmt.execute();
            stmt.close();

            String mcs = data.get("medical_conditions").toString();
            String[] medicalConditions = mcs.split(",");

            sql = "INSERT INTO patient_medical_conditions (patient_id, medical_condition, weight) VALUES(?, ?, ?)";
            stmt = Connection.get().prepareStatement(sql);
            for (int i = 0; i < medicalConditions.length; i++) {
                stmt.setString(1, id);
                stmt.setString(2, medicalConditions[i]);
                stmt.setInt(3, i + 1);
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();

            Connection.get().commit();
            return true;

        } catch (SQLException ex) {
            Connection.get().rollback();
            ex.printStackTrace();
            return false;
        } finally {
            Connection.get().setAutoCommit(true);
        }

    }

    /**
     * update a patient in database
     *
     * @param data
     * @return
     * @throws SQLException
     */
    public boolean update(HashMap<String, Object> data) throws SQLException {
        String id = data.get("id") == null ? null : data.get("id").toString();
        if (id == null) return false;

        String name = data.get("name") == null ? null : data.get("name").toString();
        String surname = data.get("surname") == null ? null : data.get("surname").toString();
        String phone = data.get("phone") == null ? null : data.get("phone").toString();
        String email = data.get("email") == null ? null : data.get("email").toString();
        String mcs = data.get("medical_conditions") == null ? null : data.get("medical_conditions").toString();

        try {
            Connection.get().setAutoCommit(false);

            // update patient
            ArrayList<String> columnSetter = new ArrayList<>();
            if (name != null) columnSetter.add("name=?");
            if (surname != null) columnSetter.add("surname=?");
            if (phone != null) columnSetter.add("phone=?");
            if (email != null) columnSetter.add("email=?");
            if (columnSetter.size() > 0) {
                String sql = String.format("UPDATE patients SET %s WHERE id=?", String.join(",", columnSetter));

                PreparedStatement stmt = Connection.get().prepareStatement(sql);
                int paramIndex = 1;
                if (name != null) stmt.setString(paramIndex++, name);
                if (surname != null) stmt.setString(paramIndex++, surname);
                if (phone != null) stmt.setString(paramIndex++, phone);
                if (email != null) stmt.setString(paramIndex++, email);
                stmt.setString(paramIndex, id);

                stmt.execute();
                stmt.close();

            }

            // update medical conditions
            if (mcs != null) {

                String[] medicalConditions = mcs.split(",");

                if (medicalConditions.length > 0) {
                    PreparedStatement stmt = Connection.get().prepareStatement("DELETE FROM patient_medical_conditions WHERE patient_id=?");
                    stmt.setString(1, id);
                    stmt.execute();
                    stmt.close();

                    String sql = "INSERT INTO patient_medical_conditions (patient_id, medical_condition, weight) VALUES(?, ?, ?)";
                    stmt = Connection.get().prepareStatement(sql);
                    for (int i = 0; i < medicalConditions.length; i++) {
                        stmt.setString(1, id);
                        stmt.setString(2, medicalConditions[i]);
                        stmt.setInt(3, i + 1);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    stmt.close();
                }
            }

            Connection.get().commit();
            Connection.get().setAutoCommit(true);

            return true;

        } catch (SQLException ex) {
            Connection.get().rollback();
            Connection.get().setAutoCommit(true);
            throw ex;
        }
    }
}
