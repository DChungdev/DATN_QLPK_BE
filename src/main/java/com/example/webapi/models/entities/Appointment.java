package com.example.webapi.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Ngày giờ hẹn
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "appointment_date", nullable = false)
    private Date appointmentDate;

    private String reason; // Lý do khám

    private String status; // 'pending', 'confirmed', 'canceled'...

    private double baseFee;

    private double totalFee;

    private String cancelReason;

    private String cancelBy;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppointmentServices> services;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    private Date updatedAt = new Date();
}
