package com.nexusfit.backend.controller;

import com.nexusfit.backend.dtos.LoginRequest;
import com.nexusfit.backend.dtos.RegisterRequest;
import com.nexusfit.backend.dtos.VerifyRequest;
import com.nexusfit.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String response = authService.Register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestBody VerifyRequest request) {
        authService.verifyAccount(request.getEmail(), request.getOtp());
        return ResponseEntity.ok("Account verified successfully.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam String email) {
        authService.resendOtp(email);
        return ResponseEntity.ok("OTP sent successfully");
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            String response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return a 401 Unauthorized for failed logins
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendForgotPasswordOtp(email);
        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email,
                                                @RequestParam String otp,
                                                @RequestParam String newPassword) {
        String response = authService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok(response);
    }
}