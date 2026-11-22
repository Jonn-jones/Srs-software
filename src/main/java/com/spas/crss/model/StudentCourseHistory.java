package com.spas.crss.model;

public class StudentCourseHistory {

    private int historyId;
    private String studentId;
    private int courseId;
    private int semesterId;
    private boolean passed;

    // Constructor for creating a new history record
    public StudentCourseHistory(
            String studentId,
            int courseId,
            int semesterId,
            boolean passed) {

        this.studentId = studentId;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.passed = passed;
    }

    // Constructor for retrieving an existing record
    public StudentCourseHistory(
            int historyId,
            String studentId,
            int courseId,
            int semesterId,
            boolean passed) {

        this.historyId = historyId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.passed = passed;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    @Override
    public String toString() {
        return "StudentCourseHistory{" +
                "historyId=" + historyId +
                ", studentId='" + studentId + '\'' +
                ", courseId=" + courseId +
                ", semesterId=" + semesterId +
                ", passed=" + (passed ? "Yes" : "No") +
                '}';
    }
}