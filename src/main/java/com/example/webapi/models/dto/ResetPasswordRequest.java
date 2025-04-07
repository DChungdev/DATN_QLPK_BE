package com.example.webapi.models.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String username;
    private String newPassword;
}
