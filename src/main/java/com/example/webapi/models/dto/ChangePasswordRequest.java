package com.example.webapi.models.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    public String username;
    public String oldPassword;
    public String newPassword;
}
