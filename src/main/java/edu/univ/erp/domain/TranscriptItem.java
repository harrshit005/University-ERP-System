package edu.univ.erp.domain;

public class TranscriptItem {

    private final String courseCode;
    private final String courseTitle;
    private final int credits;
    private final String finalGrade;

    public TranscriptItem(String courseCode, String courseTitle, int credits, String finalGrade) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.credits = credits;
        if (finalGrade != null) {
            this.finalGrade = finalGrade;
        } else {
            this.finalGrade = "IP";
        }

    }

    public String getCourseCode() {
        return courseCode;
    }
    public String getupdate(){
        return " ";
    }

    public String getCourseTitle() {
        return courseTitle;
    }
    public String getCredits() {
        return String.valueOf(credits);
    }
    public String getFinalGrade() {
        return finalGrade;
    }
}