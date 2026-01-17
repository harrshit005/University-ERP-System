package edu.univ.erp.domain;

public class InstructorListItem {
    private final int instructorId;
    private final String instructorName;

    public InstructorListItem(int instructorId, String instructorName) {
        this.instructorId = instructorId;
        this.instructorName = instructorName;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    @Override
    public String toString() {
        return instructorName;
    }
}