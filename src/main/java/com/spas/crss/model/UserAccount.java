package com.spas.crss.model;

public class UserAccount {

    private int accountId;
    private String username;
    private String passwordHash;
    private String role;
    private String accountStatus;

    private String studentId;
    private String lecturerId;
    private String codId;

    // Default constructor
    public UserAccount() {
    }

    // Constructor for creating a new account
    public UserAccount(String username, String passwordHash,
                       String role, String accountStatus,
                       String studentId, String lecturerId,
                       String codId) {

        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.accountStatus = accountStatus;
        this.studentId = studentId;
        this.lecturerId = lecturerId;
        this.codId = codId;
    }

    // Getters and Setters

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getCodId() {
        return codId;
    }

    public void setCodId(String codId) {
        this.codId = codId;
    }
}
