package edu.univ.erp.domain;


public class SectionAssignmentItem {
    private final int sectionId;
    private final String courseCode;
    private final String dayTime;
    private final String currentInstructor;

    public SectionAssignmentItem(int sectionId, String courseCode, String dayTime, String currentInstructor) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.dayTime = dayTime;
        if (currentInstructor != null) {
            this.currentInstructor = currentInstructor;
        }
        else {
            this.currentInstructor = "TBA";
        }

    }
    public int getSectionId() {
        return sectionId;
    }

    public String getupdate(){
        return " ";
    }



    @Override
    public String toString() {
        return String.format("%s (%s) - [%s]", courseCode, dayTime, currentInstructor);
        // ye object ko readable bnata h
    }
}