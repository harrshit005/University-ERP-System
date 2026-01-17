package edu.univ.erp.domain;

public class MySectionItem {

    private final int sectionId;
    private final String courseCode;
    private final String courseTitle;
    private final String dayTime;
    private final String room;
    private final int capacity;
    private final int enrolled;

    public MySectionItem(int sectionId, String courseCode, String courseTitle, String dayTime,
                         String room, int capacity, int enrolled) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.enrolled = enrolled;
    }

    public int getSectionId() {
        return sectionId;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public int getCapacity() {
        return capacity;
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

    public int getEnrolled() {
        return enrolled;
    }

    public String getEnrollment() {
        return enrolled + "/" + capacity;
    }
}