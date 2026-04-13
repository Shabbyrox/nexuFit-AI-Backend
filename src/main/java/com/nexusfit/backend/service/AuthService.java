package com.nexusfit.backend.service;

import com.nexusfit.backend.dtos.LoginRequest;
import com.nexusfit.backend.dtos.RegisterRequest;
import com.nexusfit.backend.models.Otp;
import com.nexusfit.backend.models.Users;
import com.nexusfit.backend.repository.OtpRepository;
import com.nexusfit.backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String Register(RegisterRequest request) {

        // Check if user already exists
        if(usersRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        if(usersRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already in use");
        }

        // Create unverified user
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setVerified(false);

        usersRepository.save(user);

        // Generate OTP
        String otpCode = generateOtp();

        Otp otp = new Otp();
        otp.setEmail(request.getEmail());
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);

        // Send email
        emailService.sendOtpEmail(request.getEmail(), otpCode);
        return "User registered successfully. Please check your email for OTP.";
    }

    private String generateOtp() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void resendOtp(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isVerified()) {
            throw new RuntimeException("Account already verified. Please Login.");
        }

        String newCode = generateOtp();

        // Update the existing OTP or create a new one if it was deleted
        Otp otp = otpRepository.findByEmail(email).orElse(new Otp());
        otp.setEmail(email);
        otp.setOtpCode(newCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);

        emailService.sendOtpEmail(email, newCode);
    }

    // Code to verify the OTP
    public void verifyAccount(String email, String otpCode) {
        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No OTP found for this email."));

        if(!otp.getOtpCode().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP.");
        }

        if(otp.isExpired()) {
            throw new RuntimeException("OTP expired.");
        }

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user found for this email."));

        user.setVerified(true);
        usersRepository.save(user);

        otpRepository.delete(otp);
    }

    public String login(LoginRequest request) {
        // 1. Find user by email OR username
        Users user = usersRepository.findByEmail(request.getEmailOrUsername())
                .orElseGet(() -> usersRepository.findByUsername(request.getEmailOrUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")));

        // 2. Check if the account is verified
        if (!user.isVerified()) {
            throw new RuntimeException("Please verify your account via OTP before logging in.");
        }

        // 3. Check if the password matches the encrypted one in DB
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email/username or password.");
        }

        // 4. Success
        return jwtService.generateToken(user.getEmail());
    }

    // Reset password via OTP
    public void sendForgotPasswordOtp(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user found for this email."));

        String otpCode = generateOtp();

        Otp otp = otpRepository.findByEmail(email).orElse(new Otp());
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        emailService.sendOtpEmail(email, otpCode);
    }

    public String resetPassword(String email, String otpCode, String newPassword) {
        // 1. Verify the OTP is valid for this email
        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No OTP found for this email."));

        if (!otp.getOtpCode().equals(otpCode) || otp.isExpired()) {
            throw new RuntimeException("Invalid or expired OTP.");
        }

        // 2. Find the user and update password
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        // 3. Clean up the used OTP
        otpRepository.delete(otp);

        return "Password has been reset successfully.";
    }
}