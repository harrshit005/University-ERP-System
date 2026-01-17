package edu.univ.erp;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.auth.LoginWindow;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.StudentDashboard;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {


    private static LoginWindow loginWindow;

    public static void main(String[] args) {
        // Setup modern FlatLaf UI theme for better look and feel
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Check database connectivity before starting - exit if databases unavailable
        if (!checkDatabaseConnections()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to databases. Please check configuration and try again.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        System.out.println("Database connections established successfully.");


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down... Resetting login attempts...");

            DatabaseManager dbManager = DatabaseManager.getInstance();


            // ye saare account jo locked ho gye h unhe unlock karega aur attempt firse reset krdega
            String resetSql = "UPDATE auth_db.users_auth SET failed_attempts = 0, status = 'ACTIVE' WHERE failed_attempts > 0 OR status = 'LOCKED'";

            try (Connection conn = dbManager.getAuthConnection();
                 PreparedStatement stmt = conn.prepareStatement(resetSql)) {

                int rowsAffected = stmt.executeUpdate();
                System.out.println(rowsAffected + " user accounts were reset/unlocked.");

            } catch (SQLException e) {
                System.err.println("Error resetting failed attempts on shutdown.");
                e.printStackTrace();
            }

            System.out.println("Shutting down database pools...");
            dbManager.close();
        }));

        // app start hoga login window k saath
        SwingUtilities.invokeLater(() -> {
            loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }



    public static void showDashboard(String role) {
        // login window band ho jayegi
        if (loginWindow != null) {
            loginWindow.dispose();
        }


        SwingUtilities.invokeLater(() -> {
            if ("STUDENT".equals(role)) {
                new StudentDashboard().setVisible(true);
            } else if ("INSTRUCTOR".equals(role)) {
                new InstructorDashboard().setVisible(true);
            } else if ("ADMIN".equals(role)) {
                new AdminDashboard().setVisible(true);
            } else {
                System.err.println("Unknown role: " + role);
                loginWindow = new LoginWindow(); // login window firse show hogi agr koyi error aa gya to
                loginWindow.setVisible(true);
            }
        });
    }

    public static void showLoginWindow() { // ye login window k liye h
        SwingUtilities.invokeLater(() -> {
            loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }


    private static boolean checkDatabaseConnections() {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            try (Connection authConn = dbManager.getAuthConnection();
                 Connection erpConn = dbManager.getErpConnection()) {
                boolean authValid = authConn.isValid(1);
                boolean erpValid = erpConn.isValid(1);
                if (!authValid) System.err.println("Auth DB connection is not valid.");
                if (!erpValid) System.err.println("ERP DB connection is not valid.");
                return authValid && erpValid;
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect to databases:");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during database check:");
            e.printStackTrace();
            return false;
        }

    }

}