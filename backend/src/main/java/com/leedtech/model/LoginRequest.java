package com.leedtech.model;

public class LoginRequest {
    private String studentNumber;
    private String password;

    // getters and setters
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}