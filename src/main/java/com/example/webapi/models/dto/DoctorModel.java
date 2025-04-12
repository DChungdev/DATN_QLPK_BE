package com.example.webapi.models.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorModel {
    private Long doctorId;
    private String fullName;
    private Date dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String degree;
    private Long departmentId;
    private String image;
}
