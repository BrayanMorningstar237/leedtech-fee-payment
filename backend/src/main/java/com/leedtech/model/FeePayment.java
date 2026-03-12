package com.leedtech.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment {

    private String studentNumber;
    private BigDecimal paymentAmount;
    private LocalDate paymentDate; // optional, default to today

    // Response fields
    private BigDecimal previousBalance;
    private double incentiveRate;
    private BigDecimal incentiveAmount;
    private BigDecimal newBalance;
    private LocalDate nextDueDate;

    // Getters and Setters
    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public double getIncentiveRate() {
        return incentiveRate;
    }

    public void setIncentiveRate(double incentiveRate) {
        this.incentiveRate = incentiveRate;
    }

    public BigDecimal getIncentiveAmount() {
        return incentiveAmount;
    }

    public void setIncentiveAmount(BigDecimal incentiveAmount) {
        this.incentiveAmount = incentiveAmount;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}