package com.leedtech.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StudentDashboard {
    private String studentNumber;
    private BigDecimal currentBalance;
    private BigDecimal initialBalance;          // <-- new field
    private LocalDate nextDueDate;
    private List<PaymentTransaction> paymentHistory;

    public StudentDashboard() {}

    // Updated constructor to include initialBalance
    public StudentDashboard(String studentNumber, BigDecimal currentBalance, BigDecimal initialBalance,
                            LocalDate nextDueDate, List<PaymentTransaction> paymentHistory) {
        this.studentNumber = studentNumber;
        this.currentBalance = currentBalance;
        this.initialBalance = initialBalance;
        this.nextDueDate = nextDueDate;
        this.paymentHistory = paymentHistory;
    }

    // Getters and Setters
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getInitialBalance() {          // <-- new getter
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {   // <-- new setter
        this.initialBalance = initialBalance;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public List<PaymentTransaction> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(List<PaymentTransaction> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }
}