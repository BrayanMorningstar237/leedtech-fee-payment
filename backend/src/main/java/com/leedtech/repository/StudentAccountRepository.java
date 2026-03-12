package com.leedtech.repository;

import com.leedtech.model.StudentAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAccountRepository extends JpaRepository<StudentAccount, String> {
}