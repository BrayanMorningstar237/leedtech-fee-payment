package com.leedtech.service;

import com.leedtech.model.FeePayment;
import com.leedtech.model.StudentAccount;
import com.leedtech.repository.StudentAccountRepository;
import com.leedtech.repository.PaymentTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeePaymentServiceTest {

    @Mock
    private StudentAccountRepository studentAccountRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @InjectMocks
    private FeePaymentService feePaymentService;

    private StudentAccount student;
    private FeePayment request;

    @BeforeEach
    void setUp() {
        student = new StudentAccount("STU001", new BigDecimal("800000"), "password123");
        request = new FeePayment();
        request.setStudentNumber("STU001");
    }

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual));
    }

    @Test
    void processPayment_ValidAmount100K_ShouldApply3PercentIncentive() {
        request.setPaymentAmount(new BigDecimal("100000"));
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertBigDecimalEquals(new BigDecimal("800000"), response.getPreviousBalance());
        assertEquals(0.03, response.getIncentiveRate());
        assertBigDecimalEquals(new BigDecimal("3000"), response.getIncentiveAmount());
        assertBigDecimalEquals(new BigDecimal("697000"), response.getNewBalance());
        assertNotNull(response.getNextDueDate());

        verify(studentAccountRepository).save(any(StudentAccount.class));
        verify(paymentTransactionRepository).save(any());
    }

    @Test
    void processPayment_ValidAmount500K_ShouldApply5PercentIncentive() {
        request.setPaymentAmount(new BigDecimal("500000"));
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(0.05, response.getIncentiveRate());
        assertBigDecimalEquals(new BigDecimal("25000"), response.getIncentiveAmount());
        assertBigDecimalEquals(new BigDecimal("275000"), response.getNewBalance());
    }

    @Test
    void processPayment_AmountLessThan100K_ShouldApply1PercentIncentive() {
        request.setPaymentAmount(new BigDecimal("50000"));
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(0.01, response.getIncentiveRate());
        assertBigDecimalEquals(new BigDecimal("500"), response.getIncentiveAmount());
        assertBigDecimalEquals(new BigDecimal("749500"), response.getNewBalance());
    }

    @Test
    void processPayment_AmountExactlyAtBoundary100K_ShouldApply3Percent() {
        request.setPaymentAmount(new BigDecimal("100000"));
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(0.03, response.getIncentiveRate());
    }

    @Test
    void processPayment_AmountExactlyAtBoundary500K_ShouldApply5Percent() {
        request.setPaymentAmount(new BigDecimal("500000"));
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(0.05, response.getIncentiveRate());
    }

    @Test
    void processPayment_AmountLessThanBoundary_ShouldApplyLowerTier() {
        request.setPaymentAmount(new BigDecimal("99999.99"));
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(0.01, response.getIncentiveRate());
    }

    @Test
    void processPayment_WithProvidedPaymentDate_ShouldUseThatDate() {
        LocalDate customDate = LocalDate.of(2026, 3, 14);

        request.setPaymentAmount(new BigDecimal("100000"));
        request.setPaymentDate(customDate);

        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(LocalDate.of(2026, 6, 12), response.getNextDueDate());
    }

    @Test
    void processPayment_WeekendAdjustment_SaturdayToMonday() {
        LocalDate paymentDate = LocalDate.of(2026, 4, 5);

        request.setPaymentAmount(new BigDecimal("575000"));
        request.setPaymentDate(paymentDate);

        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(LocalDate.of(2026, 7, 6), response.getNextDueDate());
    }

    @Test
    void processPayment_WeekendAdjustment_SundayToMonday() {
        LocalDate paymentDate = LocalDate.of(2026, 3, 1);

        request.setPaymentAmount(new BigDecimal("200000"));
        request.setPaymentDate(paymentDate);

        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        FeePayment response = feePaymentService.processPayment(request);

        assertEquals(LocalDate.of(2026, 6, 1), response.getNextDueDate());
    }

    @Test
    void processPayment_Overpayment_ShouldThrowException() {
        request.setPaymentAmount(new BigDecimal("900000"));
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> feePaymentService.processPayment(request));

        assertTrue(exception.getMessage().contains("exceeds current balance"));

        verify(studentAccountRepository, never()).save(any());
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void processPayment_ZeroAmount_ShouldThrowException() {
        request.setPaymentAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class,
                () -> feePaymentService.processPayment(request));
    }

    @Test
    void processPayment_NegativeAmount_ShouldThrowException() {
        request.setPaymentAmount(new BigDecimal("-100"));

        assertThrows(IllegalArgumentException.class,
                () -> feePaymentService.processPayment(request));
    }

    @Test
    void processPayment_StudentNotFound_ShouldThrowException() {
        request.setPaymentAmount(new BigDecimal("1000"));

        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> feePaymentService.processPayment(request));
    }

    @Test
    void processPayment_PaymentExactlyBalance_ShouldThrowException() {
        BigDecimal amount = new BigDecimal("776699.03");

        request.setPaymentAmount(amount);
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        assertThrows(IllegalArgumentException.class,
                () -> feePaymentService.processPayment(request));
    }
}