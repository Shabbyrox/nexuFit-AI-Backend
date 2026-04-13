package com.nexusfit.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("NexusFit AI - Your Verification Code");
        message.setText("Your OTP code is: " + otpCode);

        javaMailSender.send(message);
        System.out.println("Email sent successfully to: " + toEmail);
    }
}