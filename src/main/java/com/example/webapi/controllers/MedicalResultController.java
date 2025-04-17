package com.example.webapi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.webapi.models.dto.MedicalResultModel;
import com.example.webapi.models.entities.Appointment;
import com.example.webapi.models.entities.MedicalResult;
import com.example.webapi.services.MedicalResultService;
import com.example.webapi.services.AppointmentService;

@RestController
@RequestMapping("/api/medical-results")
public class MedicalResultController {
    @Autowired
    private MedicalResultService medicalResultService;
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public List<MedicalResultModel> getAllMedicalResults() {
        return medicalResultService.getAllMedicalResults().stream().<MedicalResultModel>map(result -> MedicalResultModel.builder()
                .id(result.getId())
                .appointment(result.getAppointment().getAppointmentId())
                .symptoms(result.getSymptoms())
                .diagnosis(result.getDiagnosis())
                .notes(result.getNotes())
                .treatmentPlan(result.getTreatmentPlan())
                .createdAt(result.getCreatedAt())
                .build()).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity getMedicalResultById(@PathVariable Long id) {
        try{
            MedicalResultModel medicalResultModel = MedicalResultModel.builder()
                    .id(medicalResultService.getMedicalResultById(id).getId())
                    .appointment(medicalResultService.getMedicalResultById(id).getAppointment().getAppointmentId())
                    .symptoms(medicalResultService.getMedicalResultById(id).getSymptoms())
                    .diagnosis(medicalResultService.getMedicalResultById(id).getDiagnosis())
                    .notes(medicalResultService.getMedicalResultById(id).getNotes())
                    .treatmentPlan(medicalResultService.getMedicalResultById(id).getTreatmentPlan())
                    .build();
            return ResponseEntity.ok(medicalResultModel);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity getMedicalResultByAppointmentId(@PathVariable Long appointmentId) {
        try{
            MedicalResultModel medicalResultModel = MedicalResultModel.builder()
                    .id(medicalResultService.getByAppointmentId(appointmentId).getId())
                    .appointment(medicalResultService.getByAppointmentId(appointmentId).getAppointment().getAppointmentId())
                    .symptoms(medicalResultService.getByAppointmentId(appointmentId).getSymptoms())
                    .diagnosis(medicalResultService.getByAppointmentId(appointmentId).getDiagnosis())
                    .notes(medicalResultService.getByAppointmentId(appointmentId).getNotes())
                    .treatmentPlan(medicalResultService.getByAppointmentId(appointmentId).getTreatmentPlan())
                    .build();
            return ResponseEntity.ok(medicalResultModel);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity getMedicalResultByPatientId(@PathVariable Long patientId) {
        try{
            List<MedicalResultModel> medicalResultModels = medicalResultService.getByPatientId(patientId).stream().<MedicalResultModel>map(result -> MedicalResultModel.builder()
                    .id(result.getId())
                    .appointment(result.getAppointment().getAppointmentId())
                    .symptoms(result.getSymptoms())
                    .diagnosis(result.getDiagnosis())
                    .notes(result.getNotes())
                    .treatmentPlan(result.getTreatmentPlan())
                    .build()).toList();
            return ResponseEntity.ok(medicalResultModels);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity createMedicalResult(@RequestBody MedicalResultModel medicalResultModel) {
        try{
            MedicalResult medicalResult = MedicalResult.builder()
                    .id(medicalResultModel.getId())
                    .symptoms(medicalResultModel.getSymptoms())
                    .diagnosis(medicalResultModel.getDiagnosis())
                    .notes(medicalResultModel.getNotes())
                    .treatmentPlan(medicalResultModel.getTreatmentPlan())
                    .build();
            if(medicalResultModel.getAppointment() != null) {
                Appointment appointment = appointmentService.getAppointmentById(medicalResultModel.getAppointment());
                medicalResult.setAppointment(appointment);
            }
            else{
                return ResponseEntity.badRequest().body("Appointment not found");
            }
            MedicalResult medicalResult1 = medicalResultService.createMedicalResult(medicalResult);
            MedicalResultModel medicalResultModel1 = MedicalResultModel.builder()
                    .id(medicalResult1.getId())
                    .appointment(medicalResult1.getAppointment().getAppointmentId())
                    .symptoms(medicalResult1.getSymptoms())
                    .diagnosis(medicalResult1.getDiagnosis())
                    .notes(medicalResult1.getNotes())
                    .treatmentPlan(medicalResult1.getTreatmentPlan())
                    .build();
            return ResponseEntity.ok(medicalResultModel1);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateMedicalResult(@PathVariable Long id, @RequestBody MedicalResultModel medicalResultModel) {
        try{
            MedicalResult medicalResult = medicalResultService.getMedicalResultById(id);
            medicalResult.setSymptoms(medicalResultModel.getSymptoms());
            medicalResult.setDiagnosis(medicalResultModel.getDiagnosis());
            medicalResult.setNotes(medicalResultModel.getNotes());
            medicalResult.setTreatmentPlan(medicalResultModel.getTreatmentPlan());
            MedicalResult medicalResult1 = medicalResultService.updateMedicalResult(id, medicalResult);
            MedicalResultModel medicalResultModel1 = MedicalResultModel.builder()
                    .id(medicalResult1.getId())
                    .appointment(medicalResult1.getAppointment().getAppointmentId())
                    .symptoms(medicalResult1.getSymptoms())
                    .diagnosis(medicalResult1.getDiagnosis())
                    .notes(medicalResult1.getNotes())
                    .treatmentPlan(medicalResult1.getTreatmentPlan())
                    .build();
            return ResponseEntity.ok(medicalResultModel1);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteMedicalResult(@PathVariable Long id) {
        try{
            medicalResultService.deleteMedicalResult(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}