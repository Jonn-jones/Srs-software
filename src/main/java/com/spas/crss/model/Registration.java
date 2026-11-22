package com.spas.crss.model;

import java.time.LocalDate;

public class Registration {

    private int registrationId;
    private String studentId;
    private int courseId;
    private int semesterId;
    private LocalDate registrationDate;
    private String registrationStatus;

    // Empty constructor
    public Registration() {
    }

    // Constructor
    public Registration(
            String studentId,
            int courseId,
            int semesterId,
            LocalDate registrationDate,
            String registrationStatus) {

        this.studentId = studentId;
        this.courseId = courseId;
        this.semesterId = semesterId;
        this.registrationDate = registrationDate;
        this.registrationStatus = registrationStatus;
    }

    // Getters and Setters

    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
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

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    // Display registration information
    @Override
    public String toString() {

        return "Registration{" +
                "registrationId=" + registrationId +
                ", studentId='" + studentId + '\'' +
                ", courseId=" + courseId +
                ", semesterId=" + semesterId +
                ", registrationDate=" + registrationDate +
                ", registrationStatus='" + registrationStatus + '\'' +
                '}';
    }
}
