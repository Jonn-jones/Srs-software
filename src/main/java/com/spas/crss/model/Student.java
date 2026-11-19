package com.spas.crss.model;

public class Student {

    private String studentId;
    private String name;
    private String programme;
    private int year;
    private String email;

    // Default constructor
    public Student() {
    }

    // Parameterized constructor
    public Student(String studentId, String name,
                   String programme, int year, String email) {

        this.studentId = studentId;
        this.name = name;
        this.programme = programme;
        this.year = year;
        this.email = email;
    }

    // Getters and Setters

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}