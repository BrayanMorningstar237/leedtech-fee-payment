package com.leedtech.controller;

import com.leedtech.model.LoginRequest;
import com.leedtech.model.LoginResponse;
import com.leedtech.model.StudentAccount;
import com.leedtech.repository.StudentAccountRepository;
import com.leedtech.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final StudentAccountRepository studentAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(StudentAccountRepository studentAccountRepository, JwtService jwtService) {
        this.studentAccountRepository = studentAccountRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        StudentAccount student = studentAccountRepository.findById(request.getStudentNumber()).orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Invalid student number or password", null, null));
        }

        if (passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            String token = jwtService.generateToken(student.getStudentNumber());
            return ResponseEntity.ok(new LoginResponse(true, "Login successful", student.getStudentNumber(), token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Invalid student number or password", null, null));
        }
    }
}