package com.example.webapi.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.webapi.models.entities.MedicalResult;

public interface MedicalResultRepository extends JpaRepository<MedicalResult, Long> {
    @Query("SELECT mr FROM MedicalResult mr WHERE mr.appointment.appointmentId = :appointmentId")
    Optional<MedicalResult> findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT mr FROM MedicalResult mr WHERE mr.appointment.patient.id = :patientId")
    List<MedicalResult> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT mr FROM MedicalResult mr WHERE mr.appointment.doctor.id = :doctorId")
    List<MedicalResult> findByDoctorId(@Param("doctorId") Long doctorId);
}
