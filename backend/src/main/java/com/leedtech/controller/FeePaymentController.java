package com.leedtech.controller;

import com.leedtech.model.FeePayment;
import com.leedtech.model.PaymentTransaction;
import com.leedtech.model.StudentAccount;
import com.leedtech.model.StudentDashboard;
import com.leedtech.service.FeePaymentService;
import com.leedtech.repository.PaymentTransactionRepository;
import com.leedtech.repository.StudentAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class FeePaymentController {

    private final FeePaymentService feePaymentService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final StudentAccountRepository studentAccountRepository;

    public FeePaymentController(FeePaymentService feePaymentService,
                                PaymentTransactionRepository paymentTransactionRepository,
                                StudentAccountRepository studentAccountRepository) {
        this.feePaymentService = feePaymentService;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.studentAccountRepository = studentAccountRepository;
    }

    @GetMapping("/")
    public String home() {
        return "LeedTech Fee Payment API is running. Use POST /api/one-time-fee-payment to make a payment.";
    }

    @PostMapping("/one-time-fee-payment")
    public ResponseEntity<?> makePayment(@RequestBody FeePayment request) {
        try {
            FeePayment response = feePaymentService.processPayment(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/students/{studentNumber}/payments")
    public ResponseEntity<?> getPaymentHistory(@PathVariable String studentNumber) {
        try {
            List<PaymentTransaction> payments = paymentTransactionRepository
                    .findByStudentNumberOrderByPaymentDateDesc(studentNumber);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/students/{studentNumber}/account")
    public ResponseEntity<?> getStudentAccount(@PathVariable String studentNumber) {
        try {
            StudentAccount student = studentAccountRepository.findById(studentNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/students/{studentNumber}/dashboard")
    public ResponseEntity<?> getStudentDashboard(@PathVariable String studentNumber) {
        try {
            // Fetch student account
            StudentAccount student = studentAccountRepository.findById(studentNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            // Fetch payment history
            List<PaymentTransaction> history = paymentTransactionRepository
                    .findByStudentNumberOrderByPaymentDateDesc(studentNumber);

            // Calculate initial balance (current balance + sum of all (payment + incentive) from history)
            BigDecimal totalReduction = BigDecimal.ZERO;
            for (PaymentTransaction tx : history) {
                totalReduction = totalReduction.add(tx.getPaymentAmount()).add(tx.getIncentiveAmount());
            }
            BigDecimal initialBalance = student.getBalance().add(totalReduction);

            // Build dashboard response with initialBalance
            StudentDashboard dashboard = new StudentDashboard(
                    student.getStudentNumber(),
                    student.getBalance(),
                    initialBalance,                     // <-- pass initialBalance
                    student.getNextDueDate(),
                    history
            );

            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}