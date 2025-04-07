package com.example.webapi.services;

import com.example.webapi.models.entities.Patient;
import com.example.webapi.repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id = " + id));
    }
    public Patient getPatientByUsername(String username) {
        return patientRepository.findByUsername(username).orElse(null);
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient patientUpdate) {
        Patient existingPatient = getPatientById(id);
        existingPatient.setFullName(patientUpdate.getFullName());
        existingPatient.setDateOfBirth(patientUpdate.getDateOfBirth());
        existingPatient.setGender(patientUpdate.getGender());
        existingPatient.setPhone(patientUpdate.getPhone());
        existingPatient.setAddress(patientUpdate.getAddress());

        return patientRepository.save(existingPatient);
    }

    public void deletePatient(Long id) {
        Patient existingPatient = getPatientById(id);
        patientRepository.delete(existingPatient);
    }
}
