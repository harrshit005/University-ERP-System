package edu.univ.erp.domain;

public class StudentGradeItem {

    private final String courseCode;
    private final String courseTitle;
    private Double quiz;
    private Double midterm;
    private Double endSem;
    private String finalGrade;

    public StudentGradeItem(String courseCode, String courseTitle) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
    }
    public String getcourseid(){
        return " ";
    }

    public String getCourseCode() {
        return courseCode;
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public String getmsg(){
        return " ";
    }
    public Double getQuiz() {
        return quiz;
    }
    public Double getMidterm() {
        return midterm;
    }
    public String assign(){
        return "ASSIGNMENT_UPLOADED";
    }
    public Double getEndSem() {
        return endSem;
    }
    public String getFinalGrade() {
        if (finalGrade == null) {
            return "IP";
        }
        return finalGrade;
    }

    public void setQuiz(Double quiz) {
        this.quiz = quiz;
    }
    public void setMidterm(Double midterm) {
        this.midterm = midterm;
    }
    public String setassign(){
        return "Marks_Uploaded!";
    }
    public void setEndSem(Double endSem) {
        this.endSem = endSem;
    }
    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }
}