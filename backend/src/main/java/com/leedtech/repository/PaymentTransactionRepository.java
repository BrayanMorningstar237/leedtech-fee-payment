package com.leedtech.repository;

import com.leedtech.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByStudentNumberOrderByPaymentDateDesc(String studentNumber);
}