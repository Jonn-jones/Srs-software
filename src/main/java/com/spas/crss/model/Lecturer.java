package com.spas.crss.model;

public class Lecturer {

    private String lecturerId;
    private String name;
    private String qualification;
    private String email;
    private String department;

    public Lecturer() {
    }

    public Lecturer(String lecturerId, String name,
                    String qualification, String email,
                    String department) {

        this.lecturerId = lecturerId;
        this.name = name;
        this.qualification = qualification;
        this.email = email;
        this.department = department;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}