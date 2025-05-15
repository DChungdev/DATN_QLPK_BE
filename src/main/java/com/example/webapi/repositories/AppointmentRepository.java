package com.example.webapi.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.webapi.models.entities.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.doctor.id = :staffId AND a.appointmentDate = :appointmentTime")
    boolean existsByStaffIdAndAppointmentTime(@Param("staffId") Long staffId, @Param("appointmentTime") Date appointmentTime);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.doctor.id = :staffId AND a.appointmentDate = :appointmentTime AND a.id != :id")
    boolean existsByStaffIdAndAppointmentTimeAndIdNot(@Param("staffId") Long staffId, @Param("appointmentTime") Date appointmentTime, @Param("id") Long id);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.patient.id = :customerId AND a.appointmentDate BETWEEN :startTime AND :endTime")
    boolean existsByCustomerIdAndAppointmentTimeBetween(@Param("customerId") Long customerId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.patient.id = :customerId AND a.appointmentDate BETWEEN :startTime AND :endTime AND a.id != :id")
    boolean existsByCustomerIdAndAppointmentTimeBetweenAndIdNot(@Param("customerId") Long customerId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("id") Long id);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId ORDER BY a.appointmentDate")
    List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId ORDER BY a.appointmentDate")
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.doctor.id = :staffId AND a.appointmentDate = :appointmentTime AND a.status != :status")
    boolean existsByStaffIdAndAppointmentTimeAndStatusNot(@Param("staffId") Long staffId, @Param("appointmentTime") Date appointmentTime, @Param("status") String status);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a WHERE a.patient.id = :customerId AND a.appointmentDate BETWEEN :startTime AND :endTime AND a.status != :status")
    boolean existsByCustomerIdAndAppointmentTimeBetweenAndStatusNot(@Param("customerId") Long customerId, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("status") String status);
}
