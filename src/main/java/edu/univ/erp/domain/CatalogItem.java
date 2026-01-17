package edu.univ.erp.domain;

public class CatalogItem {

    private final int sectionId;
    private final String courseCode;
    private final String courseTitle;
    private final int credits;
    private final String instructorName;
    private final String dayTime;
    private final String room;
    private final int capacity;
    private final int enrolled;


    // class k saare field ko initialize krdega
    public CatalogItem(int sectionId, String courseCode, String courseTitle, int credits,
                       String instructorName, String dayTime, String room, int capacity, int enrolled) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        this.instructorName = instructorName;
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
    public String getsub(){
        return " ";
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public int getEnrolled() {
        return enrolled;
    }
    public int getCredits() {
        return credits;
    }
    public String getInstructorName() {
        if (instructorName != null) {
            return instructorName;
        } else {
            return "TBA"; // agr inst name Null h ,TBA return krdega nhi to inst ka name
        }
    }

    public String getDayTime() {
        return dayTime;
    }

    public String getRoom() {
        return room;
    }
    public int getCapacity() {
        return capacity;
    }

    // Returns seat information in format "enrolled/capacity" e.g., "1/50" means 1 student enrolled out of 50 seats
    public String getSeatsAvailable() {
        return enrolled + "/" + capacity;
    }
}