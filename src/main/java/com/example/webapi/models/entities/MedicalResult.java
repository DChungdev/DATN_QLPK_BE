package com.example.webapi.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "medical_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    private String symptoms;       // Triệu chứng
    private String diagnosis;      // Chẩn đoán
    private String notes;          // Ghi chú
    private String treatmentPlan;  // Phác đồ điều trị
    private Date createdAt = new Date();
}
