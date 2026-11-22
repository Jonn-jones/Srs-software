package com.spas.crss.model;

public class CourseAssignment {

    private int assignmentId;
    private int courseId;
    private String lecturerId;
    private int semesterId;

    // Default constructor
    public CourseAssignment() {
    }

    // Constructor
    public CourseAssignment(int courseId, String lecturerId, int semesterId) {
        this.courseId = courseId;
        this.lecturerId = lecturerId;
        this.semesterId = semesterId;
    }

    // Getters and Setters
    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    @Override
    public String toString() {
        return "CourseAssignment{" +
                "assignmentId=" + assignmentId +
                ", courseId=" + courseId +
                ", lecturerId='" + lecturerId + '\'' +
                ", semesterId=" + semesterId +
                '}';
    }
}