package com.example.webapi.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelAppointmentRequest {
    private String cancelReason;
    private String cancelBy;
}
