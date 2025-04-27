package com.example.webapi.models.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientModel {
    private Long patientId;
    private String fullName;
    private Date dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String image;
    private Long accountId;
}
