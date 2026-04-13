package com.nexusfit.backend.dtos;

import lombok.Data;

@Data
public class VerifyRequest {
    private String otp;
    private String email;
}