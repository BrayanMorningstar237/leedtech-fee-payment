package com.leedtech.model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String studentNumber;
    private String token; // new field

    public LoginResponse(boolean success, String message, String studentNumber, String token) {
        this.success = success;
        this.message = message;
        this.studentNumber = studentNumber;
        this.token = token;
    }

    // getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}