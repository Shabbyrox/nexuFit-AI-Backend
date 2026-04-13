package com.nexusfit.backend.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String emailOrUsername;
    private String password;
}