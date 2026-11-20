package com.spas.crss.model;

import java.sql.Date;

public class AcademicSemester {

    // 1. Declare variables
    private int semesterId;
    private String academicYear;
    private String semesterName;
    private Date startDate;
    private Date endDate;
    private String semesterStatus;

    // 2. Default constructor
    public AcademicSemester() {
    }

    // 3. Parameterized constructor
    public AcademicSemester(int semesterId, String academicYear,
                            String semesterName, Date startDate,
                            Date endDate, String semesterStatus) {

        this.semesterId = semesterId;
        this.academicYear = academicYear;
        this.semesterName = semesterName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.semesterStatus = semesterStatus;
    }

    // 4. Getters and Setters

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSemesterStatus() {
        return semesterStatus;
    }

    public void setSemesterStatus(String semesterStatus) {
        this.semesterStatus = semesterStatus;
    }

    // 5. Display semester information
    @Override
    public String toString() {
        return "AcademicSemester{" +
                "semesterId=" + semesterId +
                ", academicYear='" + academicYear + '\'' +
                ", semesterName='" + semesterName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", semesterStatus='" + semesterStatus + '\'' +
                '}';
    }
}
