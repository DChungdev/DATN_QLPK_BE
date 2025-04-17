package com.example.webapi.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webapi.models.entities.MedicalResult;
import com.example.webapi.repositories.MedicalResultRepository;

@Service
public class MedicalResultService {
    @Autowired
    private MedicalResultRepository medicalResultRepository;
    
    public MedicalResult createMedicalResult(MedicalResult medicalResult) {
        Optional<MedicalResult> medicalResult1 = medicalResultRepository.findByAppointmentId(medicalResult.getAppointment().getAppointmentId());
        if(medicalResult1.isPresent()){
            throw new RuntimeException("Medical result already exists for appointment ID: " + medicalResult.getAppointment().getAppointmentId());
        }
        medicalResult.setCreatedAt(new Date());
        return medicalResultRepository.save(medicalResult);
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
        medicalResultRepository.delete(medicalResult);
    }
}
