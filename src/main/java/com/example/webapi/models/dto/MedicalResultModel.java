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
public class MedicalResultModel {
    private Long id;
    private Long appointment;
    private String symptoms;       // Triệu chứng
    private String diagnosis;      // Chẩn đoán
    private String notes;          // Ghi chú
    private String treatmentPlan;  // Phác đồ điều trị
    private Date createdAt = new Date();
}
