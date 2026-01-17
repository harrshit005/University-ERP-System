package edu.univ.erp.domain;

public class GradebookItem {

    private final int enrollmentId;
    private final String studentName;
    private final String rollNumber;
    private Double quiz;
    private Double midterm;
    private Double endSem;
    private String finalGrade;


    // isme bhi initialize krdega
    public GradebookItem(int enrollmentId, String studentName, String rollNumber) {
        this.enrollmentId = enrollmentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
    }


    public int getEnrollmentId() {
        return enrollmentId;
    }
    public String getStudentName() {
        return studentName;
    }
    public String getRollNumber() {
        return rollNumber;
    }

    public Double getQuiz() {
        return quiz;
    }
    public Double getMidterm() {
        return midterm;
    }
    public Double getEndSem() {
        return endSem;
    }
    public String getFinalGrade() {
        return finalGrade;
    }

    public void setQuiz(Double quiz) {
        this.quiz = quiz;
    }
    public String getupdate(){
        return " ";
    }
    public void setMidterm(Double midterm) {
        this.midterm = midterm;
    }
    public void setEndSem(Double endSem) {
        this.endSem = endSem;
    }
    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }
}