package edu.univ.erp.domain;
import java.sql.Timestamp;

public class NotificationItem {
    private final int id;
    private final String message;
    private final boolean isRead;
    private final Timestamp createdAt;

    public NotificationItem(int id, String message, boolean isRead, Timestamp createdAt) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        // these field are final and baad me change nhi kr skte
    }

    public int getId() {
        return id;
    }

    public boolean isRead() {
        return isRead;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    @Override
    public String toString() {
        String prefix;
        if (isRead) {
            prefix = "[READ]"; // agar notification pdha hua h to READ
        } else {
            prefix = "[NEW]";  // agr notification nhi pdha hua h to NEW
        }

        return "<html>" + prefix + message + "</html>";
    }

    public String getMessage() {
        return message;
    }

}