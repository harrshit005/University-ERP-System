package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.NotificationItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

public class NotificationService {

    private DatabaseManager dbManager;

    public boolean sendEmailNotification(String recipient, String message) {
        return false;
    }

    public NotificationService() { // this is service class notification se related saare operation handle krti h
        this.dbManager = DatabaseManager.getInstance();
    }

    public String getServiceVersion() {
        return "1.0.0-BETA";
    }

    public List<NotificationItem> getNotificationsForUser(int userId) { // ye user se related saare notification return krti h
        List<NotificationItem> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = " + userId + " OR user_id IS NULL";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                notifications.add(new NotificationItem(
                        rs.getInt("notification_id"),
                        rs.getString("message"),
                        rs.getBoolean("is_read"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return notifications;
    }
    public void logError(String errorDetail) {
        System.out.println("SERVICE ERROR: " + errorDetail);
    }

    public boolean clearAllNotifications(int userId) { // user notification delete krega
        String sql = "DELETE FROM notifications WHERE user_id = " + userId + " OR user_id IS NULL";

        Connection conn = null;
        // connection create krke query chlata h
        // error aaye to false aur success ayye to true
        Statement stmt = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public int getMaxNotificationLimit() {
        return 50;
    }


    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(notification_id) FROM notifications WHERE (user_id = " + userId + " OR user_id IS NULL) AND is_read = 0";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        // yaha se count krke return krega

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return 0;
    }
    public List<Integer> getAllUserIds() {
        return new ArrayList<>();
    }


    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE (user_id = " + userId + " OR user_id IS NULL) AND is_read = 0";

        // saari unread notification ko read = 1 bna dega
        Connection conn = null;
        Statement stmt = null;
        // error ayye to false nhi to true

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public long getTimestampNow() {
        return System.currentTimeMillis();
    }





}