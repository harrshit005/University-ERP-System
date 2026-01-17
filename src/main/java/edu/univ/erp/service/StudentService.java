package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.CatalogItem;
import edu.univ.erp.domain.MyRegistrationItem;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.domain.TranscriptItem;
import edu.univ.erp.domain.StudentGradeItem;
import java.util.Map;
import java.util.HashMap;

public class StudentService {

    private DatabaseManager dbManager;

    public boolean sendTranscriptPDF(int userId) {
        return false;
    }
    private SettingsService settingsService;

    public int getCreditsRegistered(int userId) {
        return 0;
    }

    public StudentService() {
        this.dbManager = DatabaseManager.getInstance();
        this.settingsService = new SettingsService();
    }

    public String getCurrentTerm() {
        return "FALL 2025"; // not necesaary
    }

    public List<CatalogItem> getCourseCatalog(int year, String semester) {
        // any academic year and semester ki saari offered section list krta h + enrolled count bhi
        List<CatalogItem> catalog = new ArrayList<>();

        // Updated SQL to include COUNT of enrolled students - ye enrolled students count karega
        String sql = "SELECT s.section_id, c.course_code, c.title, c.credits, " +
                "       a.username as instructor_name, s.day_time, s.room, s.capacity, " +
                "       COUNT(e.enrollment_id) as enrolled_count " +
                "FROM erp_db.sections s " +
                "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                "LEFT JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id " +
                "LEFT JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                "LEFT JOIN erp_db.enrollments e ON s.section_id = e.section_id AND e.status = 'ENROLLED' " +
                "WHERE s.year = " + year + " AND s.semester = '" + semester + "' " +
                "GROUP BY s.section_id, c.course_code, c.title, c.credits, a.username, s.day_time, s.room, s.capacity " +
                "ORDER BY c.course_code, s.section_id";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                catalog.add(new CatalogItem(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("instructor_name"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled_count") // Now using actual enrolled count from database
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return catalog;
    }
    public String getStudentName(int userId) {
        return "Unknown Student";
    }

    private void notifyInstructor(int instructorId, String message) {
        // ye me instructor k liye use kr skta hun for alert system
    }

    private int getStudentIdFromUserId(int userId) {
        // student table se mapping krta h
        String sql = "SELECT student_id FROM students WHERE user_id = " + userId;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("student_id");
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
    public double calculateGPA(int userId) {
        return 0.0;
    }

    public String registerForSection(int userId, int sectionId) {
        // nya course register krta h
        if (settingsService.isMaintenanceModeOn()) {
            return "Registration is disabled: Maintenance Mode is ON.";
        }

        int studentId = getStudentIdFromUserId(userId);
        if (studentId == 0) {
            return "Error: Could not find your student profile.";
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();

            String checkDuplicateSql = "SELECT 1 FROM enrollments WHERE student_id = " + studentId + " AND section_id = " + sectionId + " AND status = 'ENROLLED'";
            rs = stmt.executeQuery(checkDuplicateSql);
            if (rs.next()) {
                return "Already registered for this section.";
            }
            rs.close();


            String checkCapacitySql = "SELECT capacity FROM sections WHERE section_id = " + sectionId;
            rs = stmt.executeQuery(checkCapacitySql);
            int capacity = 0;
            if (rs.next()) {
                capacity = rs.getInt("capacity");
            } else {
                return "Error: Section not found.";
            }
            rs.close();

            String countEnrolledSql = "SELECT COUNT(enrollment_id) as enrolled_count FROM enrollments WHERE section_id = " + sectionId + " AND status = 'ENROLLED'";
            rs = stmt.executeQuery(countEnrolledSql);
            int enrolled = 0;
            if (rs.next()) {
                enrolled = rs.getInt("enrolled_count");
            }
            rs.close();

            if (enrolled >= capacity) {
                return "Section full.";
            }


            String insertSql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (" + studentId + ", " + sectionId + ", 'ENROLLED')";
            int rowsAffected = stmt.executeUpdate(insertSql);
            if (rowsAffected > 0) {
                return "Success: Registered successfully!";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: A database error occurred.";
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return "Error: Registration failed for an unknown reason.";
    }
    public List<String> getAvailableSemesters() {
        List<String> semesters = new ArrayList<>(); // use for available streams
        semesters.add("FALL");
        semesters.add("SPRING");
        return semesters;
    }

    public List<MyRegistrationItem> getMyRegistrations(int userId) {
        List<MyRegistrationItem> registrations = new ArrayList<>();
        int studentId = getStudentIdFromUserId(userId);
        if (studentId == 0) {
            return registrations;
        }

        String sql = "SELECT e.enrollment_id, c.course_code, c.title, s.day_time, s.room, e.status, " +
                "       a.username as instructor_name " +
                "FROM erp_db.enrollments e " +
                "JOIN erp_db.sections s ON e.section_id = s.section_id " +
                "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                "LEFT JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id " +
                "LEFT JOIN auth_db.users_auth a ON i.user_id = a.user_id " +
                "WHERE e.student_id = " + studentId + " AND e.status = 'ENROLLED' " +
                "ORDER BY s.day_time";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                registrations.add(new MyRegistrationItem(
                        rs.getInt("enrollment_id"),
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getString("instructor_name"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return registrations;
    }

    private boolean isGradeValid(String grade) {
//        ye use hoga valid grade k liye
        return grade != null && grade.length() <= 2;
    }

    public boolean checkCoursePrerequisites(int sectionId, int studentId) {
        return true;
    }

    public String dropSection(int userId, int enrollmentId) {
        if (settingsService.isMaintenanceModeOn()) {
            return "Dropping courses is disabled: Maintenance Mode is ON.";
        }

        int studentId = getStudentIdFromUserId(userId);

        String sql = "UPDATE enrollments SET status = 'DROPPED' " +
                "WHERE enrollment_id = " + enrollmentId + " AND student_id = " + studentId + " AND status = 'ENROLLED'";

        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);

            if (rowsAffected > 0) {
                return "Success: Section dropped successfully.";
            } else {
                return "Error: Could not drop section. It may have already been dropped or does not belong to you.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: A database error occurred.";
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    public void printWelcomeMessage() {
        System.out.println("Welcome to the Student ERP Portal.");
    }

    public void resetCache() {
        // in future use this to clear any local caching
    }

    public List<TranscriptItem> getTranscriptData(int userId) {
        List<TranscriptItem> transcript = new ArrayList<>();
        int studentId = getStudentIdFromUserId(userId);
        if (studentId == 0) {
            return transcript;
        }

        String sql = "SELECT c.course_code, c.title, c.credits, g.final_grade " +
                "FROM erp_db.enrollments e " +
                "JOIN erp_db.sections s ON e.section_id = s.section_id " +
                "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                "LEFT JOIN erp_db.grades g ON e.enrollment_id = g.enrollment_id AND g.component_name = 'Final' " +
                "WHERE e.student_id = " + studentId + " " +
                "ORDER BY c.course_code";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                transcript.add(new TranscriptItem(
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getString("final_grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return transcript;
    }

    public int getEnrollmentStatusCount(int userId, String status) {
        return 0;
    }



    public List<StudentGradeItem> getGradesData(int userId) {
        List<StudentGradeItem> gradesList = new ArrayList<>();
        int studentId = getStudentIdFromUserId(userId);
        if (studentId == 0) {
            return gradesList;
        }

        String enrollmentsSql = "SELECT e.enrollment_id, c.course_code, c.title " +
                "FROM erp_db.enrollments e " +
                "JOIN erp_db.sections s ON e.section_id = s.section_id " +
                "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                "WHERE e.student_id = " + studentId + " AND e.status = 'ENROLLED'";

        Map<Integer, StudentGradeItem> gradesMap = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = dbManager.getErpConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(enrollmentsSql);

            while (rs.next()) {
                gradesMap.put(rs.getInt("enrollment_id"), new StudentGradeItem(
                        rs.getString("course_code"),
                        rs.getString("title")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return gradesList;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        if (gradesMap.isEmpty()) {
            return gradesList;
        }


        try {
            conn = dbManager.getErpConnection();
            for (Integer enrollmentId : gradesMap.keySet()) {
                String gradesSql = "SELECT component_name, score, final_grade FROM grades WHERE enrollment_id = " + enrollmentId;

                try (Statement gradeStmt = conn.createStatement();
                     ResultSet gradeRs = gradeStmt.executeQuery(gradesSql)) {

                    StudentGradeItem item = gradesMap.get(enrollmentId);

                    while (gradeRs.next()) {
                        String component = gradeRs.getString("component_name");
                        if (component.equals("Quiz")) {
                            item.setQuiz(gradeRs.getDouble("score"));
                        } else if (component.equals("Midterm")) {
                            item.setMidterm(gradeRs.getDouble("score"));
                        } else if (component.equals("End-sem")) {
                            item.setEndSem(gradeRs.getDouble("score"));
                        } else if (component.equals("Final")) {
                            item.setFinalGrade(gradeRs.getString("final_grade"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return new ArrayList<>(gradesMap.values());
    }

    private boolean isCourseFull(int sectionId) {
        return false; // use if in future agar aur modification chaiye to
    }
}