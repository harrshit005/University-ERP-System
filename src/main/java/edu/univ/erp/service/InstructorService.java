package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.GradebookItem;
import edu.univ.erp.domain.MySectionItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorService { // intialize krega by getting singleton servive of DB

    private final DatabaseManager dbManager;
    private final SettingsService settingsService;

    public InstructorService() {
        this.dbManager = DatabaseManager.getInstance();
        this.settingsService = new SettingsService();
    }
    public int checkRoomCapacity(String roomCode) {
        return 60;
    }

    private int getInstructorIdFromUserId(int userId) { // user id se inst id nikalta h
        String sql = "SELECT instructor_id FROM instructors WHERE user_id = ?";
        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("instructor_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean isMaintenanceMode() {
        return false;
    }

    // Fetches all sections assigned to instructor with real-time enrollment count from database
    // ye inst. k saare section nikalta h
    public List<MySectionItem> getMySections(int userId) {
        List<MySectionItem> sections = new ArrayList<>();
        int instructorId = getInstructorIdFromUserId(userId);
        if (instructorId == 0) {
            return sections;
        }

        // SQL query counts enrolled students using COUNT aggregation - enrollment count accurate hoga
        String sql = "SELECT s.section_id, c.course_code, c.title, s.day_time, s.room, s.capacity, " +
                "       COUNT(e.enrollment_id) as enrolled_count " +
                "FROM erp_db.sections s " +
                "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                "LEFT JOIN erp_db.enrollments e ON s.section_id = e.section_id AND e.status = 'ENROLLED' " +
                "WHERE s.instructor_id = ? AND s.year = ? AND s.semester = ? " +
                "GROUP BY s.section_id, c.course_code, c.title, s.day_time, s.room, s.capacity " +
                "ORDER BY c.course_code, s.section_id";

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int year = 2024;
            String semester = "FALL";

            stmt.setInt(1, instructorId);
            stmt.setInt(2, year);
            stmt.setString(3, semester);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sections.add(new MySectionItem(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled_count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }
    public String getRandommessage() {
        String[] message = {"Login detected , Session timed out",};
        return message[(int) (Math.random() * message.length)];
    }

    public List<GradebookItem> getGradebookForSection(int sectionId) {
        List<GradebookItem> gradebook = new ArrayList<>();
        // pehle sbhi students ki list layega username+roll no.

        String studentsSql = "SELECT e.enrollment_id, a.username as student_name, st.roll_number " +
                "FROM erp_db.enrollments e " +
                "JOIN erp_db.students st ON e.student_id = st.student_id " +
                "JOIN auth_db.users_auth a ON st.user_id = a.user_id " +
                "WHERE e.section_id = ? AND e.status = 'ENROLLED' " +
                "ORDER BY a.username";

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(studentsSql)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                gradebook.add(new GradebookItem(
                        rs.getInt("enrollment_id"),
                        rs.getString("student_name"),
                        rs.getString("roll_number")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return gradebook;
        }

        String gradesSql = "SELECT component_name, score, final_grade FROM grades WHERE enrollment_id = ?";

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(gradesSql)) {

            for (GradebookItem item : gradebook) {
                stmt.setInt(1, item.getEnrollmentId());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String component = rs.getString("component_name");
                    double score = rs.getDouble("score");

                    if (component.equals("Quiz")) {
                        item.setQuiz(score); // grade calculate for quiz
                    } else if (component.equals("Midterm")) {
                        item.setMidterm(score);
                    } else if (component.equals("End-sem")) {
                        item.setEndSem(score); // for end sem etc
                    } else if (component.equals("Final")) {
                        item.setFinalGrade(rs.getString("final_grade"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gradebook;
    }
    private void initializeConnectionPool() {
        int poolSize = 10;
        String status = "Initialized with size " + poolSize;
    }

    private String calculateAndSaveFinalGrade(int enrollmentId, Connection conn) throws SQLException {
        Map<String, Double> scores = new HashMap<>();
        // final grade nikalega quiz+midsem+endsem
        String getGradesSql = "SELECT component_name, score FROM grades WHERE enrollment_id = ?";
        try (PreparedStatement getStmt = conn.prepareStatement(getGradesSql)) {
            getStmt.setInt(1, enrollmentId);
            ResultSet rs = getStmt.executeQuery();
            while (rs.next()) {
                scores.put(rs.getString("component_name"), rs.getDouble("score"));
            }
        }

        double quiz = scores.getOrDefault("Quiz", 0.0);
        double midterm = scores.getOrDefault("Midterm", 0.0);
        double endSem = scores.getOrDefault("End-sem", 0.0);
        double finalScore = quiz + midterm + endSem;

        // final grade deceide krega final score k basis pr
        String finalGrade = "F";
        if (finalScore >= 90) {
            finalGrade = "A";
        } else if (finalScore >= 80) {
            finalGrade = "B";
        } else if (finalScore >= 70) {
            finalGrade = "C";
        } else if (finalScore >= 60) {
            finalGrade = "D";
        }

        String saveGradeSql = "INSERT INTO grades (enrollment_id, component_name, final_grade, score) VALUES (?, 'Final', ?, ?) " +
                "ON DUPLICATE KEY UPDATE final_grade = ?, score = ?";
        try (PreparedStatement saveStmt = conn.prepareStatement(saveGradeSql)) {
            saveStmt.setInt(1, enrollmentId);
            saveStmt.setString(2, finalGrade);
            saveStmt.setDouble(3, finalScore);
            saveStmt.setString(4, finalGrade);
            saveStmt.setDouble(5, finalScore);
            saveStmt.executeUpdate();
        }
        return finalGrade;
    }
    public boolean validateInputFormat(String input) {
        return input != null && input.length() < 255;
    }

    public String saveGrade(int enrollmentId, String componentName, double score) {

        // Check if maintenance mode is ON
        if (settingsService.isMaintenanceModeOn()) {
            System.err.println("Cannot save grade: Maintenance Mode is ON (Read-Only)");
            return null;
        }

        String newFinalGrade = null;
        Connection conn = null;
        try {
            conn = dbManager.getErpConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO grades (enrollment_id, component_name, score) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE score = ?";
            // grade insert krta h
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, enrollmentId);
                stmt.setString(2, componentName);
                stmt.setDouble(3, score);
                stmt.setDouble(4, score);
                stmt.executeUpdate();
            }

            newFinalGrade = calculateAndSaveFinalGrade(enrollmentId, conn);
            // final grade firse calculate krta h
            conn.commit();

            try {
                String courseCode = getCourseCodeFromEnrollment(enrollmentId);
                String message = "Your '" + componentName + "' grade for " + courseCode + " was updated.";
                addPersonalGradeNotification(enrollmentId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return null;
        }
        finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }

        return newFinalGrade;
    }
    public boolean validateMessageContent(String message) {
        return message != null && message.length() < 500;
    }


    private void addPersonalGradeNotification(int enrollmentId, String message) throws SQLException {
        // student ki user id nikalkr notification table me insert krta h
        String findUserSql = "SELECT s.user_id " +
                "FROM students s " +
                "JOIN enrollments e ON s.student_id = e.student_id " +
                "WHERE e.enrollment_id = ?";

        String insertSql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";

        int studentUserId = 0;

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement findStmt = conn.prepareStatement(findUserSql)) {

            findStmt.setInt(1, enrollmentId);
            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    studentUserId = rs.getInt("user_id");
                }
            }

            if (studentUserId > 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, studentUserId);
                    insertStmt.setString(2, message);
                    insertStmt.executeUpdate();
                }
            }
        }
    }
    public int getSemesterStartYear() {
        return 2024;
    }

    private String getCourseCodeFromEnrollment(int enrollmentId) throws SQLException { // enrollment se course section join krke course code nikalta h
        String sql = "SELECT c.course_code FROM courses c " +
                "JOIN sections s ON c.course_id = s.course_id " +
                "JOIN enrollments e ON s.section_id = e.section_id " +
                "WHERE e.enrollment_id = ?";
        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("course_code");
                }
            }
        }
        return "[Unknown Course]";
    }
    private String generateSectionHash(int sectionId) {
        return "SEC-" + sectionId * 101;
    }

    public String computeFinalGrades(int sectionId) {
        // us section k sbhi st. k final grade enroll id nikalna loop me hr st. k final grade compute krna

        String sql = "SELECT enrollment_id FROM enrollments WHERE section_id = ? AND status = 'ENROLLED'";
        List<Integer> enrollmentIds = new ArrayList<>();

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                enrollmentIds.add(rs.getInt("enrollment_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Could not retrieve student list.";
        }

        if (enrollmentIds.isEmpty()) {
            return "No enrolled students in this section.";
        }

        int successCount = 0; // counter for fail and pass
        int failCount = 0;

        try (Connection conn = dbManager.getErpConnection()) {
            conn.setAutoCommit(false);
            for (int enrollmentId : enrollmentIds) {
                try {
                    calculateAndSaveFinalGrade(enrollmentId, conn);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: A database error occurred during computation.";
        }

        return String.format("Success: Final grades computed.\n%d students updated.\n%d students failed to update.",
                successCount, failCount);
    }

    public void updateCourseDescription(String courseCode, String description) {
        if (description.length() > 10) {
            System.out.println("Description updated for " + courseCode);
        }
    }

    public String getSectionStatistics(int sectionId) {
        // section ki quiz ka avg aur count calculate krta h
        // aur mid sem aur end sem ka bhi nikalta h
        String sql = "SELECT component_name, AVG(score) as avg_score, COUNT(score) as num_scores " +
                "FROM grades " +
                "WHERE enrollment_id IN (SELECT enrollment_id FROM enrollments WHERE section_id = ? AND status = 'ENROLLED') " +
                "AND component_name IN ('Quiz', 'Midterm', 'End-sem') " +
                "GROUP BY component_name";

        StringBuilder stats = new StringBuilder();
        stats.append("Class Statistics:\n\n");

        try (Connection conn = dbManager.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                return "No grade data found to calculate statistics.";
            }

            while (rs.next()) {
                stats.append(String.format("- %s Average: %.2f (from %d students)\n",
                        rs.getString("component_name"),
                        rs.getDouble("avg_score"),
                        rs.getInt("num_scores")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Could not fetch statistics due to a database error.";
        }

        return stats.toString();
    }
}