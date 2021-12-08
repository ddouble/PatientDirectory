package com.sample.data;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connection {
    private static java.sql.Connection connection = null;

    public static java.sql.Connection get() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            ensureDatabaseStructure(connection);
        }

        return connection;
    }

    private static boolean ensureDatabaseStructure(java.sql.Connection dbConnection) throws SQLException {
        Statement stmt = dbConnection.createStatement();
        stmt.setQueryTimeout(30);  // set timeout to 30 sec.

        ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='patients'");
        try {
            if (!rs.next()) {
                rs.close();

                String sql[] = {
                        "CREATE TABLE patients("
                                + "id VARCHAR (20) NOT NULL, "
                                + "name VARCHAR (20) NOT NULL, "
                                + "surname VARCHAR (20) NOT NULL, "
                                + "phone VARCHAR (20) NOT NULL, "
                                + "email VARCHAR (20) NOT NULL, "
                                + "PRIMARY KEY (ID))",
                        "CREATE TABLE patient_medical_conditions("
                                + "patient_id VARCHAR (20) NOT NULL, "
                                + "medical_condition VARCHAR (20) NOT NULL, "
                                + "weight INTEGER, "
                                + "PRIMARY KEY (patient_id, medical_condition))",
                        "CREATE INDEX patient_medical_conditions_medical_condition_index "
                                + "ON patient_medical_conditions (medical_condition)"
                };

                Connection.get().setAutoCommit(false);
                boolean isOk = true;
                for (int i = 0; i < sql.length; i++) {
                    try {
                        stmt.execute(sql[i]);
                    } catch (SQLException ex) {
                        isOk = false;
                        Connection.get().rollback();
                        break;
                    }
                }
                if (isOk) Connection.get().commit();
                Connection.get().setAutoCommit(true);

                return isOk;
            }

        } finally {
            rs.close();
            stmt.close();
        }
        return true;
    }

    public static void close() throws SQLException {
        if (connection != null) connection.close();
    }
}
