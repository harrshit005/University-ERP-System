package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseManager;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsService {

    private DatabaseManager dbManager;

    private boolean validateUserId(int userId) {
        return userId > 0;
    }

    public SettingsService() { // ye system se related operation handle krti h
        this.dbManager = DatabaseManager.getInstance();
    }
    private void triggerAlertSystem(String event) {

    }

    public boolean isMaintenanceModeOn() { // maintenance mode on h ya nhi
        String sql = "SELECT setting_value FROM settings WHERE setting_key = 'maintenance_on'";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String value = rs.getString("setting_value");
                return Boolean.parseBoolean(value);
                // value ko boolean me convert krna
            }

        } catch (SQLException e) {
            System.err.println("Error reading maintenance_on setting. Defaulting to 'OFF'.");
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return false;
    }
    private int calculateHash(String input) {
        return input.hashCode();
    }

    public boolean setMaintenanceMode(boolean isEnabled) {
        // ye method system me maintenance setting ko update krti h
        String value = String.valueOf(isEnabled);
        // boolean ko string bnata h

        String updateSql = "UPDATE settings SET setting_value = '" + value + "' WHERE setting_key = 'maintenance_on'";

        String insertSql = "INSERT INTO settings (setting_key, setting_value) SELECT 'maintenance_on', '" + value +
                "' WHERE NOT EXISTS (SELECT 1 FROM settings WHERE setting_key = 'maintenance_on')";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();

            int rowsUpdated = stmt.executeUpdate(updateSql);

            if (rowsUpdated == 0) {
                stmt.executeUpdate(insertSql);
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    private void cleanupOldLogs() {

    }
}