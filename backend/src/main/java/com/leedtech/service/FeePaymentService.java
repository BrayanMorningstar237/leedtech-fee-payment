package com.leedtech.service;

import com.leedtech.model.FeePayment;
import com.leedtech.model.StudentAccount;
import com.leedtech.model.PaymentTransaction;
import com.leedtech.repository.StudentAccountRepository;
import com.leedtech.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class FeePaymentService {

    private final StudentAccountRepository studentAccountRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    public FeePaymentService(StudentAccountRepository studentAccountRepository,
                             PaymentTransactionRepository paymentTransactionRepository) {
        this.studentAccountRepository = studentAccountRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Transactional
    public FeePayment processPayment(FeePayment request) {
        // Validate payment amount
        if (request.getPaymentAmount() == null || request.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment must be greater than zero");
        }

        // Find student
        StudentAccount student = studentAccountRepository.findById(request.getStudentNumber())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        BigDecimal previousBalance = student.getBalance();
        double rate = calculateIncentiveRate(request.getPaymentAmount());
        BigDecimal incentive = request.getPaymentAmount().multiply(BigDecimal.valueOf(rate));
        BigDecimal newBalance = previousBalance.subtract(request.getPaymentAmount().add(incentive));

        // 🛑 PREVENT NEGATIVE BALANCE
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Payment amount plus incentive exceeds current balance");
        }

        LocalDate paymentDate = request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now();
        LocalDate nextDueDate = calculateNextDueDate(paymentDate);

        // Update student account
        student.setBalance(newBalance);
        student.setNextDueDate(nextDueDate);
        studentAccountRepository.save(student);

        // Save payment transaction for history
        PaymentTransaction transaction = new PaymentTransaction(
                student.getStudentNumber(),
                request.getPaymentAmount(),
                incentive,
                rate,
                paymentDate,
                nextDueDate
        );
        paymentTransactionRepository.save(transaction);

        // Prepare response
        FeePayment response = new FeePayment();
        response.setStudentNumber(student.getStudentNumber());
        response.setPreviousBalance(previousBalance);
        response.setPaymentAmount(request.getPaymentAmount());
        response.setIncentiveRate(rate);
        response.setIncentiveAmount(incentive);
        response.setNewBalance(newBalance);
        response.setNextDueDate(nextDueDate);

        return response;
    }

    private double calculateIncentiveRate(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(500_000)) >= 0) return 0.05;
        if (amount.compareTo(BigDecimal.valueOf(100_000)) >= 0) return 0.03;
        return 0.01;
    }

    private LocalDate calculateNextDueDate(LocalDate paymentDate) {
        LocalDate nextDue = paymentDate.plus(90, ChronoUnit.DAYS);
        if (nextDue.getDayOfWeek() == DayOfWeek.SATURDAY) {
            nextDue = nextDue.plusDays(2);
        } else if (nextDue.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nextDue = nextDue.plusDays(1);
        }
        return nextDue;
    }
}