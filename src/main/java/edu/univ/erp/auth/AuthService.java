package edu.univ.erp.auth;

import edu.univ.erp.data.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private final DatabaseManager dbManager;

    // data base manager taaki hr jagah ek hi data base use ho
    public AuthService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean isSessionValid(String token) {
        if (token == null || token.isEmpty()) { // check krega token null ya empty to nhi agr h to session invalid
            return false;
        }
        return true;
    }

    public String login(String username, String password) {
        String sql = "SELECT user_id, password_hash, role, status, failed_attempts FROM users_auth WHERE username = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getAuthConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return "INVALID"; // agr username invalid h to
            }

            int userId = rs.getInt("user_id");
            String storedHash = rs.getString("password_hash");
            String role = rs.getString("role");
            String status = rs.getString("status");
            int failedAttempts = rs.getInt("failed_attempts");

            if (status.equals("LOCKED")) {
                return "LOCKED"; // attempt khtm ho jayye tb account locked
            }

            int reset;

            if (BCrypt.checkpw(password, storedHash)) { // ye hashed password compare krta h
                if (failedAttempts > 0) {
                    String resetSql = "UPDATE users_auth SET failed_attempts = 0 WHERE user_id = ?";
                    PreparedStatement updateStmt = null; // shi pass hone pr rest attempt krdega aur user session create krta h
                    try {
                        updateStmt = conn.prepareStatement(resetSql);
                        updateStmt.setInt(1, userId);
                        updateStmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        if (updateStmt != null) try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                    }
                }
                UserSession.getInstance().createSession(userId, username, role);
                return role;
            }

            int noattempt;

            int newAttempts = failedAttempts + 1;

            if (newAttempts >= 5) {
                String lockSql = "UPDATE users_auth SET failed_attempts = ?, status = 'LOCKED' WHERE user_id = ?";
                PreparedStatement updateStmt = null;
                try {
                    updateStmt = conn.prepareStatement(lockSql);
                    updateStmt.setInt(1, newAttempts);
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (updateStmt != null) try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                }
                return "JUST_LOCKED"; // agar saare attempt khtm ho jayenge to account lock ho jayega
            } else {
                String updateSql = "UPDATE users_auth SET failed_attempts = ? WHERE user_id = ?";
                PreparedStatement updateStmt = null;
                try {
                    updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setInt(1, newAttempts);
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (updateStmt != null) try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                }
                int attemptsRemaining = 5 - newAttempts;
                return "INVALID ATTEMPTS " + attemptsRemaining; // ye btayega ki kitne attempts bcche h abhi
            }

        } catch (SQLException e) {
            System.err.println("Error during login for user: " + username);
            e.printStackTrace();
            return "ERROR";
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public void resetFailedAttempts(String username) {
        System.out.println("Simulated reset for: " + username);
    }

    public void logout() {
        UserSession.getInstance().clearSession();
    }

    public String changePassword(int userId, String oldPassword, String newPassword) {
        String sqlGetHash = "SELECT password_hash FROM users_auth WHERE user_id = ?";
        String storedHash = null; // password change krta h

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        PreparedStatement updateStmt = null;

        try {
            conn = dbManager.getAuthConnection();

            stmt = conn.prepareStatement(sqlGetHash);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return "Error: User not found.";
            }

            storedHash = rs.getString("password_hash");

            if (!BCrypt.checkpw(oldPassword, storedHash)) {
                return "Error: Old password incorrect.";
            }

            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

            String sqlUpdateHash = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
            updateStmt = conn.prepareStatement(sqlUpdateHash);
            updateStmt.setString(1, newHash);
            updateStmt.setInt(2, userId);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Success: Password changed successfully.";
            } else {
                return "Error: Failed to update password.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: A database error occurred.";
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (updateStmt != null) try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

}