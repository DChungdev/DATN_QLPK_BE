package com.example.webapi.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import com.example.webapi.models.entities.AppointmentServices;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private Date appointmentDate;

    private String reason; // Lý do khám

    private String status; // 'pending', 'confirmed', 'canceled'...

    private double baseFee;

    private double totalFee;

    private String cancelReason;

    private String cancelBy;

    private List<Long> serviceIds;
} 