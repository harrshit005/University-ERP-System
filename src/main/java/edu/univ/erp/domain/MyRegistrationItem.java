package edu.univ.erp.domain;

public class MyRegistrationItem {

    private final int enrollmentId;
    private final String courseCode;
    private final String courseTitle;
    private final String dayTime;
    private final String room;
    private final String instructorName;
    private final String status;

    public MyRegistrationItem(int enrollmentId, String courseCode, String courseTitle,
                              String dayTime, String room, String instructorName, String status) {
        this.enrollmentId = enrollmentId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.dayTime = dayTime;
        this.room = room;
        this.instructorName = instructorName;
        this.status = status;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }
    public String getExam(){
        return "Exam";
    }
    public String getCourseCode() {
        return courseCode;
    }
    public String getCourseTitle() {
        return courseTitle;
    }

    public String getupdate(){
        return " ";
    }
    public String getDayTime() {
        return dayTime;
    }
    public String getRoom() {
        return room;
    }
    public String getTutroom(){
        return " ";
    }
    public String getInstructorName() {
        if (instructorName != null) {
            return instructorName;
        } else {
            return "TBA";
        }
    }
    public String getStatus() {
        return status;
    }
}