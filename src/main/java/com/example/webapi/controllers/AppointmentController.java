package com.example.webapi.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.webapi.models.dto.AppointmentRequest;
import com.example.webapi.models.entities.Appointment;
import com.example.webapi.services.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;
    @GetMapping
    public ResponseEntity getAllAppointments1() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            List<AppointmentRequest> appointmentRequests = appointments.stream()
                .<AppointmentRequest>map(appointment -> AppointmentRequest.builder()
                    .appointmentId(appointment.getAppointmentId())
                    .patientId(appointment.getPatient().getPatientId())
                    .doctorId(appointment.getDoctor().getDoctorId()) 
                    .appointmentDate(appointment.getAppointmentDate())
                    .reason(appointment.getReason())
                    .status(appointment.getStatus())
                    .baseFee(appointment.getBaseFee())
                    .build())
                .collect(Collectors.toList());
            return ResponseEntity.ok(appointmentRequests);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<AppointmentRequest> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            AppointmentRequest request = AppointmentRequest.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatient().getPatientId())
                .doctorId(appointment.getDoctor().getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .baseFee(appointment.getBaseFee())
                .build();
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity createAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        try {
            Appointment appointment = appointmentService.createAppointment(appointmentRequest);
            AppointmentRequest response = AppointmentRequest.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatient().getPatientId())
                .doctorId(appointment.getDoctor().getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .baseFee(appointment.getBaseFee())
                .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequest appointmentRequest) {
        try {
            Appointment appointment = appointmentService.updateAppointment(id, appointmentRequest);
            AppointmentRequest response = AppointmentRequest.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatient().getPatientId())
                .doctorId(appointment.getDoctor().getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .baseFee(appointment.getBaseFee())
                .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}