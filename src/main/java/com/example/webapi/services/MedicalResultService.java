package com.example.webapi.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webapi.models.entities.MedicalResult;
import com.example.webapi.repositories.MedicalResultRepository;
import com.example.webapi.services.AppointmentService;
import com.example.webapi.models.dto.AppointmentRequest;
import com.example.webapi.models.entities.Appointment;

@Service
public class MedicalResultService {
    @Autowired
    private MedicalResultRepository medicalResultRepository;
    
    @Autowired
    private AppointmentService appointmentService;
    
    public MedicalResult createMedicalResult(MedicalResult medicalResult) {
        Optional<MedicalResult> medicalResult1 = medicalResultRepository.findByAppointmentId(medicalResult.getAppointment().getAppointmentId());
        if(medicalResult1.isPresent()){
            throw new RuntimeException("Medical result already exists for appointment ID: " + medicalResult.getAppointment().getAppointmentId());
        }
        medicalResult.setCreatedAt(new Date());
        
        // Save the medical result
        MedicalResult savedResult = medicalResultRepository.save(medicalResult);
        
        // Update appointment status to completed
        Long appointmentId = medicalResult.getAppointment().getAppointmentId();
        com.example.webapi.models.dto.AppointmentRequest appointmentRequest = new com.example.webapi.models.dto.AppointmentRequest();
        appointmentRequest.setStatus("completed");
        appointmentService.updateAppointment(appointmentId, appointmentRequest);
        
        return savedResult;
    }

    public List<MedicalResult> getAllMedicalResults() {
        return medicalResultRepository.findAll();
    }

    public MedicalResult getMedicalResultById(Long id) {
        return medicalResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MedicalResult not found"));
    }
    
    public MedicalResult getByAppointmentId(Long appointmentId) {
        return medicalResultRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Medical result not found for appointment ID: " + appointmentId));
    }
    
    public List<MedicalResult> getByPatientId(Long patientId) {
        return medicalResultRepository.findByPatientId(patientId);
    }
    
    public MedicalResult updateMedicalResult(Long id, MedicalResult updatedResult) {
        MedicalResult existingResult = getMedicalResultById(id);
        
        // Update fields
        if (updatedResult.getDiagnosis() != null) {
            existingResult.setDiagnosis(updatedResult.getDiagnosis());
        }
        if (updatedResult.getSymptoms() != null) {
            existingResult.setSymptoms(updatedResult.getSymptoms());
        }
        if (updatedResult.getNotes() != null) {
            existingResult.setNotes(updatedResult.getNotes());
        }
        if (updatedResult.getTreatmentPlan() != null) {
            existingResult.setTreatmentPlan(updatedResult.getTreatmentPlan());
        }
        
        existingResult.setCreatedAt(new Date());
        return medicalResultRepository.save(existingResult);
    }
    
    public void deleteMedicalResult(Long id) {
        MedicalResult medicalResult = getMedicalResultById(id);
        
        // Xóa liên kết với Appointment để tránh vấn đề với orphanRemoval
        Appointment appointment = medicalResult.getAppointment();
        if (appointment != null) {
            appointment.setMedicalResult(null);
            appointment.setStatus("confirmed");
            AppointmentRequest appointmentRequest = new AppointmentRequest();
            appointmentRequest.setStatus("confirmed");
            appointmentService.updateAppointment(appointment.getAppointmentId(), appointmentRequest);
        }
        
        // Xóa MedicalResult
        // medicalResultRepository.delete(medicalResult);
    }
}
