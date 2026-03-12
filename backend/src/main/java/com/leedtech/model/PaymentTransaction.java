package com.leedtech.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentNumber;

    private BigDecimal paymentAmount;

    private BigDecimal incentiveAmount;

    private double incentiveRate;

    private LocalDate paymentDate;

    private LocalDate nextDueDate;

    // Constructors
    public PaymentTransaction() {}

    public PaymentTransaction(String studentNumber, BigDecimal paymentAmount,
                              BigDecimal incentiveAmount, double incentiveRate,
                              LocalDate paymentDate, LocalDate nextDueDate) {
        this.studentNumber = studentNumber;
        this.paymentAmount = paymentAmount;
        this.incentiveAmount = incentiveAmount;
        this.incentiveRate = incentiveRate;
        this.paymentDate = paymentDate;
        this.nextDueDate = nextDueDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getIncentiveAmount() {
        return incentiveAmount;
    }

    public void setIncentiveAmount(BigDecimal incentiveAmount) {
        this.incentiveAmount = incentiveAmount;
    }

    public double getIncentiveRate() {
        return incentiveRate;
    }

    public void setIncentiveRate(double incentiveRate) {
        this.incentiveRate = incentiveRate;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}