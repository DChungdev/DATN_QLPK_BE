package com.example.webapi.services;

import java.util.List;
import java.util.Date;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webapi.models.entities.Appointment;
import com.example.webapi.models.dto.AppointmentRequest;
import com.example.webapi.repositories.AppointmentRepository;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired 
    private PatientService patientService;

    private static final int MAX_DAYS_AHEAD = 30;
    private static final int MINUTES_THRESHOLD = 30; // Minimum minutes between appointments for same patient

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    public Appointment createAppointment(AppointmentRequest request) {
        // Validate appointment date
        Date appointmentDate = request.getAppointmentDate();
        Date now = new Date();
        
        // Check if appointment is in the past
        if (appointmentDate.before(now)) {
            throw new RuntimeException("Cannot book appointments in the past");
        }
        
        // Check if appointment is within allowed time range
        long daysBetween = ChronoUnit.DAYS.between(now.toInstant(), appointmentDate.toInstant());
        if (daysBetween > MAX_DAYS_AHEAD) {
            throw new RuntimeException("Appointments can only be booked up to " + MAX_DAYS_AHEAD + " days in advance");
        }

        // Check for existing appointment with same doctor at same time
        boolean hasDoctorAppointment = appointmentRepository.existsByStaffIdAndAppointmentTime(
            request.getDoctorId(), 
            appointmentDate
        );
        if (hasDoctorAppointment) {
            throw new RuntimeException("Doctor already has an appointment at this time");
        }

        // Check for existing appointment with same patient at nearby time
        Date startTime = new Date(appointmentDate.getTime() - (MINUTES_THRESHOLD * 60 * 1000));
        Date endTime = new Date(appointmentDate.getTime() + (MINUTES_THRESHOLD * 60 * 1000));
        boolean hasPatientAppointment = appointmentRepository.existsByCustomerIdAndAppointmentTimeBetween(
            request.getPatientId(),
            startTime,
            endTime
        );
        if (hasPatientAppointment) {
            throw new RuntimeException("Patient already has an appointment within " + MINUTES_THRESHOLD + " minutes of this time");
        }

        // Create new appointment
        Appointment appointment = new Appointment();
        appointment.setPatient(patientService.getPatientById(request.getPatientId()));
        appointment.setDoctor(doctorService.findById(request.getDoctorId()));
        appointment.setAppointmentDate(appointmentDate);
        appointment.setReason(request.getReason());
        appointment.setStatus("pending");
        appointment.setBaseFee(request.getBaseFee());
        appointment.setCreatedAt(new Date());
        appointment.setUpdatedAt(new Date());
        
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = getAppointmentById(id);
        
        // Validate appointment date for updates as well
        Date appointmentDate = request.getAppointmentDate();
        Date now = new Date();
        
        if (appointmentDate.before(now)) {
            throw new RuntimeException("Cannot update appointment to a past time");
        }
        
        long daysBetween = ChronoUnit.DAYS.between(now.toInstant(), appointmentDate.toInstant());
        if (daysBetween > MAX_DAYS_AHEAD) {
            throw new RuntimeException("Appointments can only be scheduled up to " + MAX_DAYS_AHEAD + " days in advance");
        }

        // Check for existing appointment with same doctor at same time (excluding current appointment)
        boolean hasDoctorAppointment = appointmentRepository.existsByStaffIdAndAppointmentTimeAndIdNot(
            request.getDoctorId(), 
            appointmentDate,
            id
        );
        if (hasDoctorAppointment) {
            throw new RuntimeException("Doctor already has an appointment at this time");
        }

        // Check for existing appointment with same patient at nearby time (excluding current appointment)
        Date startTime = new Date(appointmentDate.getTime() - (MINUTES_THRESHOLD * 60 * 1000));
        Date endTime = new Date(appointmentDate.getTime() + (MINUTES_THRESHOLD * 60 * 1000));
        boolean hasPatientAppointment = appointmentRepository.existsByCustomerIdAndAppointmentTimeBetweenAndIdNot(
            request.getPatientId(),
            startTime,
            endTime,
            id
        );
        if (hasPatientAppointment) {
            throw new RuntimeException("Patient already has an appointment within " + MINUTES_THRESHOLD + " minutes of this time");
        }

        appointment.setPatient(patientService.getPatientById(request.getPatientId()));
        appointment.setDoctor(doctorService.findById(request.getDoctorId()));
        appointment.setAppointmentDate(appointmentDate);
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus());
        appointment.setBaseFee(request.getBaseFee());
        appointment.setUpdatedAt(new Date());

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.delete(appointment);
    }
}