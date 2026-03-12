package com.leedtech.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leedtech.model.FeePayment;
import com.leedtech.model.PaymentTransaction;
import com.leedtech.model.StudentAccount;
import com.leedtech.model.StudentDashboard;
import com.leedtech.repository.PaymentTransactionRepository;
import com.leedtech.repository.StudentAccountRepository;
import com.leedtech.service.FeePaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FeePaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeePaymentService feePaymentService;

    @MockBean
    private StudentAccountRepository studentAccountRepository;

    @MockBean
    private PaymentTransactionRepository paymentTransactionRepository;

    private FeePayment validRequest;
    private FeePayment validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new FeePayment();
        validRequest.setStudentNumber("STU001");
        validRequest.setPaymentAmount(new BigDecimal("200000"));

        validResponse = new FeePayment();
        validResponse.setStudentNumber("STU001");
        validResponse.setPaymentAmount(new BigDecimal("200000"));
        validResponse.setPreviousBalance(new BigDecimal("800000"));
        validResponse.setIncentiveRate(0.03);
        validResponse.setIncentiveAmount(new BigDecimal("6000"));
        validResponse.setNewBalance(new BigDecimal("594000"));
        validResponse.setNextDueDate(LocalDate.now().plusDays(90));
    }

    @Test
    @WithMockUser
    void makePayment_ValidRequest_ShouldReturnOk() throws Exception {
        when(feePaymentService.processPayment(any(FeePayment.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/one-time-fee-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentNumber").value("STU001"))
                .andExpect(jsonPath("$.newBalance").value(594000))
                .andExpect(jsonPath("$.incentiveRate").value(0.03));
    }

    @Test
    @WithMockUser
    void makePayment_InvalidAmount_ShouldReturnBadRequest() throws Exception {
        FeePayment invalidRequest = new FeePayment();
        invalidRequest.setStudentNumber("STU001");
        invalidRequest.setPaymentAmount(new BigDecimal("-100"));

        when(feePaymentService.processPayment(any(FeePayment.class)))
                .thenThrow(new IllegalArgumentException("Payment must be greater than zero"));

        mockMvc.perform(post("/api/one-time-fee-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Payment must be greater than zero"));
    }

    @Test
    @WithMockUser
    void makePayment_StudentNotFound_ShouldReturnBadRequest() throws Exception {
        when(feePaymentService.processPayment(any(FeePayment.class)))
                .thenThrow(new IllegalArgumentException("Student not found"));

        mockMvc.perform(post("/api/one-time-fee-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student not found"));
    }

    @Test
    @WithMockUser
    void getStudentAccount_ExistingStudent_ShouldReturnOk() throws Exception {
        StudentAccount student = new StudentAccount("STU001", new BigDecimal("800000"), "password123");
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/students/STU001/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentNumber").value("STU001"))
                .andExpect(jsonPath("$.balance").value(800000));
    }

    @Test
    @WithMockUser
    void getStudentAccount_NonExistingStudent_ShouldReturnNotFound() throws Exception {
        when(studentAccountRepository.findById("STU999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/STU999/account"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getStudentDashboard_ShouldReturnOk() throws Exception {
        StudentAccount student = new StudentAccount("STU001", new BigDecimal("594000"), "password123");
        when(studentAccountRepository.findById("STU001")).thenReturn(Optional.of(student));

        List<PaymentTransaction> transactions = List.of(
                new PaymentTransaction("STU001", new BigDecimal("200000"), new BigDecimal("6000"), 0.03, LocalDate.now(), LocalDate.now().plusDays(90))
        );
        when(paymentTransactionRepository.findByStudentNumberOrderByPaymentDateDesc("STU001"))
                .thenReturn(transactions);

        var response = mockMvc.perform(get("/api/students/STU001/dashboard"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        StudentDashboard dashboard = objectMapper.readValue(response, StudentDashboard.class);

        assertEquals(new BigDecimal("594000"), dashboard.getCurrentBalance());
        assertEquals(new BigDecimal("800000"), dashboard.getInitialBalance());
        assertEquals(1, dashboard.getPaymentHistory().size());
    }
}