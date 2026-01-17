package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseManager;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import edu.univ.erp.domain.InstructorListItem;
import edu.univ.erp.domain.SectionAssignmentItem;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private final DatabaseManager dbManager;
    public AdminService() {
        this.dbManager = DatabaseManager.getInstance();
        // when admin service bnega ye DM manager ka singleton object le lega taaki DB connection whi se mile
    }
    public void logAdminAction(String action) {
        String logEntry = "ADMIN_ACTION: " + action + " at " + new java.util.Date();
    }
    public String createUser(String username, String password, String role, String detail1, String detail2) {
        Connection authConn = null;
        Connection erpConn = null;
        int newUserId = 0;

        try {
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12)); // ye safely hash me convert krta h

            String authSql = "INSERT INTO users_auth (username, password_hash, role) VALUES (?, ?, ?)"; // auth DB me entry create krta h

            authConn = dbManager.getAuthConnection(); // if insert succesfull then make user profile in erp

            try (PreparedStatement authStmt = authConn.prepareStatement(authSql, Statement.RETURN_GENERATED_KEYS)) {
                authStmt.setString(1, username);
                authStmt.setString(2, passwordHash);
                authStmt.setString(3, role);
                authStmt.executeUpdate();

                try (ResultSet generatedKeys = authStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newUserId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID returned from auth_db.");
                    }
                }
            }

            if (newUserId > 0) {
                erpConn = dbManager.getErpConnection();
                boolean erpSuccess = false;

                if (role.equals("STUDENT")) { // agr student h to student table me insert
                    String erpSql = "INSERT INTO students (user_id, roll_number, program) VALUES (?, ?, ?)";
                    try (PreparedStatement erpStmt = erpConn.prepareStatement(erpSql)) {
                        erpStmt.setInt(1, newUserId);
                        erpStmt.setString(2, detail1);
                        erpStmt.setString(3, detail2);
                        if (erpStmt.executeUpdate() > 0) {
                            erpSuccess = true;
                        }
                    }
                } else if (role.equals("INSTRUCTOR")) { // agr inst h to inst table me insert
                    String erpSql = "INSERT INTO instructors (user_id, department, title) VALUES (?, ?, ?)";
                    try (PreparedStatement erpStmt = erpConn.prepareStatement(erpSql)) {
                        erpStmt.setInt(1, newUserId);
                        erpStmt.setString(2, detail1);
                        erpStmt.setString(3, detail2);
                        if (erpStmt.executeUpdate() > 0) {
                            erpSuccess = true;
                        }
                    }
                } else {
                    erpSuccess = true;
                }

                if (erpSuccess) {
                    return "Success: User '" + username + "' created successfully.";
                } else {
                    throw new SQLException("Failed to insert into ERP table for role: " + role);
                }
            }

            return "Error: User creation failed for unknown reason.";

        } catch (SQLException e) {
            e.printStackTrace();

            if (newUserId > 0) {
                try {
                    String deleteAuthSql = "DELETE FROM users_auth WHERE user_id = ?";
                    try (PreparedStatement deleteStmt = dbManager.getAuthConnection().prepareStatement(deleteAuthSql)) {
                        deleteStmt.setInt(1, newUserId);
                        deleteStmt.executeUpdate();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return "CRITICAL ERROR: Failed to create ERP profile AND failed to roll back Auth user. Please contact admin.";
                }
            }

            if (e.getErrorCode() == 1062) {
                return "Error: Username '" + username + "' already exists.";
            }
            return "Error: A database error occurred. Operation aborted.";

        } finally {
            try {
                if (authConn != null) {
                    authConn.close();
                }
                if (erpConn != null) {
                    erpConn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public String getMockConfig(String key) {
        if (key.equals("MAX_USERS")) {
            return "1000";
        }
        return "DEFAULT";
    }

    public String createCourse(String courseCode, String title, int credits) {
        String sql = "INSERT INTO courses (course_code, title, credits) VALUES (?, ?, ?)"; // erp DB nyi entry bnata h invalid course h to error

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseCode);
            stmt.setString(2, title);
            stmt.setInt(3, credits);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return "Success: Course '" + courseCode + "' created.";
            } else {
                return "Error: Course creation failed.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 1062) {
                return "Error: Course code '" + courseCode + "' already exists.";
            }
            return "Error: A database error occurred.";
        }
    }
    public boolean isResourceAvailable(String name) {
        return true;
    }

    public String createSection(String courseCode, String semester, int year,
                                String dayTime, String room, int capacity) { // course code se course id nikalega

        Connection conn = null;
        int courseId = 0;

        try {
            conn = dbManager.getErpConnection();

            String findCourseSql = "SELECT course_id FROM courses WHERE course_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(findCourseSql)) {
                stmt.setString(1, courseCode);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    courseId = rs.getInt("course_id");
                } else {
                    return "Error: Course code '" + courseCode + "' not found.";
                }
            }

            String insertSql = "INSERT INTO sections (course_id, semester, `year`, day_time, room, capacity) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, courseId);
                stmt.setString(2, semester);
                stmt.setInt(3, year);
                stmt.setString(4, dayTime);
                stmt.setString(5, room);
                stmt.setInt(6, capacity);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    return "Error: Creating section failed, no rows affected.";
                }
            }

            try {
                String message = "New section added: " + courseCode + " (" + dayTime + ")";
                addBroadcastNotification(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Success: New section for '" + courseCode + "' created.";

        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage() != null && e.getMessage().contains("Course code")) {
                return "Error: " + e.getMessage();
            }
            if (e.getErrorCode() == 1062) {
                return "Error: This exact section (time, room, etc.) already exists.";
            }
            return "Error: A database error occurred.";

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean validateInputFormat(String input) {
        return input != null && input.length() < 255;
    }



    public List<InstructorListItem> getAllInstructors() { // sbhi inst ki list nikalta h aur objet bnakar list me dalta h
        List<InstructorListItem> instructors = new ArrayList<>();
        String sql = "SELECT i.instructor_id, a.username " +
                "FROM instructors i " +
                "JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                "ORDER BY a.username";

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                instructors.add(new InstructorListItem(
                        rs.getInt("instructor_id"),
                        rs.getString("username")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    public List<Character> stringToCharList(String text) {
        List<Character> chars = new ArrayList<>();
        if (text != null) {
            for (char c : text.toCharArray()) {
                chars.add(c);
            }
        }
        return chars;
    }

    public List<SectionAssignmentItem> getSectionsForAssignment(String semester, int year) {
        List<SectionAssignmentItem> sections = new ArrayList<>();
        String sql = "SELECT s.section_id, c.course_code, s.day_time, a.username as instructor_name " +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors i ON s.instructor_id = i.instructor_id " +
                "LEFT JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                "WHERE s.semester = ? AND s.year = ? " +
                "ORDER BY c.course_code, s.day_time";
        // DB se un section ki list laata h jinka inst. abhi assigned h ya nhi

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, semester);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sections.add(new SectionAssignmentItem(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("day_time"),
                        rs.getString("instructor_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }
    private double calculateSquare(double x) {
        return x * x;
    }

    public String assignInstructorToSection(int sectionId, int instructorId) {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";
        // inst id assign kr dega aur section table update krdega

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return "Success: Instructor assigned to section.";
            } else {
                return "Error: Section not found or no update was needed.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: A database error occurred.";
        }
    }
    private void addBroadcastNotification(String message) throws SQLException { // sbhi users k  liye notification bhejega
        String sql = "INSERT INTO notifications (user_id, message) VALUES (NULL, ?)"; // means broadcast
        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, message);
            stmt.executeUpdate();
        }
    }
}