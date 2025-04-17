package com.example.webapi.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.webapi.models.dto.AddServicesRequest;
import com.example.webapi.models.dto.AppointmentRequest;
import com.example.webapi.models.dto.CancelAppointmentRequest;
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
                    .totalFee(appointment.getTotalFee())
                    .serviceIds(appointment.getServices().stream()
                        .map(as -> as.getService().getId())
                        .collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList());
            return ResponseEntity.ok(appointmentRequests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
                .totalFee(appointment.getTotalFee())
                .serviceIds(appointment.getServices().stream()
                        .map(as -> as.getService().getId())
                        .collect(Collectors.toList()))
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
                .serviceIds(appointment.getServices().stream()
                        .map(as -> as.getService().getId())
                        .collect(Collectors.toList()))
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

    @PostMapping("/{id}/cancel")
    public ResponseEntity cancelAppointment(@PathVariable Long id, @RequestBody CancelAppointmentRequest request) {
        try {
            appointmentService.cancelAppointment(id, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // @PostMapping("/{id}/add-services")
    // public ResponseEntity addServicesToAppointment(@PathVariable Long id, @RequestBody AddServicesRequest request) {
    //     try {
    //         appointmentService.addServicesToAppointment(id, request);
    //         return ResponseEntity.ok().build();
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
            List<AppointmentRequest> appointmentRequests = appointments.stream()
                .<AppointmentRequest>map(appointment -> AppointmentRequest.builder()
                    .appointmentId(appointment.getAppointmentId())
                    .patientId(appointment.getPatient().getPatientId())
                    .doctorId(appointment.getDoctor().getDoctorId())
                    .appointmentDate(appointment.getAppointmentDate())
                    .reason(appointment.getReason())
                    .status(appointment.getStatus())
                    .baseFee(appointment.getBaseFee())
                    .serviceIds(appointment.getServices().stream()
                        .map(as -> as.getService().getId())
                        .collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList());
            return ResponseEntity.ok(appointmentRequests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}/dates")
    public ResponseEntity<?> getDoctorAppointmentDates(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
            List<Date> appointmentDates = appointments.stream()
                .map(Appointment::getAppointmentDate)
                .collect(Collectors.toList());
            return ResponseEntity.ok(appointmentDates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}