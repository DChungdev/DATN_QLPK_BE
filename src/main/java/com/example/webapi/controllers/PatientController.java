package com.example.webapi.controllers;

import com.example.webapi.models.entities.Patient;
import com.example.webapi.models.dto.PatientModel;
import com.example.webapi.services.AccountService;
import com.example.webapi.services.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/patients")
public class PatientController {
    @Autowired
    private PatientService patientService;
    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.findAll();
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/findbyUsername/{username}")
    public ResponseEntity getPatientByUsername(@PathVariable String username) {
        try{
            Patient patient = patientService.getPatientByUsername(username);
            if(patient != null) {
                return ResponseEntity.ok(patient);
            }
            else {
                return ResponseEntity.notFound().build();
            }
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity createPatient(@RequestBody Patient patient) {
        try{
            Patient created = patientService.createPatient(patient);
            return ResponseEntity.ok(created);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePatient(@PathVariable Long id,@RequestBody Patient patient) {
        try {
            Patient updated = patientService.updatePatient(id,patient);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePatient(@PathVariable Long id) {
        try{
            patientService.deletePatient(id);

            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getbydoctorid/{doctorId}")
    public ResponseEntity<?> getPatientsByDoctor(@PathVariable Long doctorId) {
        try {
            List<Patient> patients = patientService.getPatientsByDoctorId(doctorId);
            List<PatientModel> patientModels = patients.stream()
                .map(patient -> PatientModel.builder()
                    .patientId(patient.getPatientId())
                    .fullName(patient.getFullName()) 
                    .dateOfBirth(patient.getDateOfBirth())
                    .gender(patient.getGender())
                    .phone(patient.getPhone())
                    .address(patient.getAddress())
                    .image(patient.getImage())
                    .accountId(patient.getAccount() != null ? patient.getAccount().getAccountId() : null)
                    .build())
                .collect(Collectors.toList());
            return ResponseEntity.ok(patientModels);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
