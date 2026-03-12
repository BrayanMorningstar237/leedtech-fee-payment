package com.leedtech.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class StudentAccount {

    @Id
    private String studentNumber;

    private BigDecimal balance;

    private LocalDate nextDueDate;

    private String password; // hashed password

    public StudentAccount() {}

    public StudentAccount(String studentNumber, BigDecimal balance, String password) {
        this.studentNumber = studentNumber;
        this.balance = balance;
        this.password = password;
        this.nextDueDate = null;
    }

    // Getters and Setters
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}