package com.example.webapi.controllers;

import com.example.webapi.models.dto.DoctorModel;
import com.example.webapi.models.entities.Department;
import com.example.webapi.models.entities.Doctor;
import com.example.webapi.repositories.DoctorRepository;
import com.example.webapi.services.DepartmentService;
import com.example.webapi.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/doctors")
public class DoctorController {
    @Autowired
    DoctorService doctorService;
    @Autowired
    DepartmentService departmentService;

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping
    public List<DoctorModel> getDoctors() {
        return doctorService.findAll().stream().<DoctorModel>map(doctor -> DoctorModel.builder()
                .doctorId(doctor.getDoctorId())
                .fullName(doctor.getFullName())
                .dateOfBirth(doctor.getDateOfBirth())
                .gender(doctor.getGender())
                .phone(doctor.getPhone())
                .address(doctor.getAddress())
                .degree(doctor.getDegree())
                .departmentId(doctor.getDepartment() != null ? doctor.getDepartment().getDepartmentId() : null)
                .image(doctor.getImage())
                .build()).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity getDoctor(@PathVariable Long id) {
        try{
            Doctor doctor = doctorService.findById(id);
            DoctorModel doctorModel = DoctorModel.builder()
                    .doctorId(doctor.getDoctorId())
                    .fullName(doctor.getFullName())
                    .dateOfBirth(doctor.getDateOfBirth())
                    .gender(doctor.getGender())
                    .phone(doctor.getPhone())
                    .address(doctor.getAddress())
                    .degree(doctor.getDegree())
                    .departmentId(doctor.getDepartment() != null ? doctor.getDepartment().getDepartmentId() : null)
                    .image(doctor.getImage())
                    .build();
            return ResponseEntity.ok(doctorModel);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findbyUsername/{username}")
    public ResponseEntity getDoctorByUsername(@PathVariable String username) {
        try{
            Doctor doctor = doctorService.getDoctorByUsername(username);
            if(doctor != null) {
                DoctorModel doctorModel = DoctorModel.builder()
                    .doctorId(doctor.getDoctorId())
                    .fullName(doctor.getFullName())
                    .dateOfBirth(doctor.getDateOfBirth())
                    .gender(doctor.getGender())
                    .phone(doctor.getPhone())
                    .address(doctor.getAddress())
                    .degree(doctor.getDegree())
                    .departmentId(doctor.getDepartment() != null ? doctor.getDepartment().getDepartmentId() : null)
                    .image(doctor.getImage())
                    .build();
                return ResponseEntity.ok(doctorModel);
            }
            else return ResponseEntity.notFound().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/findbyDepartmentId/{id}")
    public ResponseEntity getDoctorsByDepartmentId(@PathVariable Long id) {
        try{
            List<DoctorModel> doctors = doctorService.getDoctorsByDepartmentId(id).stream().<DoctorModel>map(doctor -> DoctorModel.builder()
            .doctorId(doctor.getDoctorId())
            .fullName(doctor.getFullName())
            .dateOfBirth(doctor.getDateOfBirth())
            .gender(doctor.getGender())
            .phone(doctor.getPhone())
            .address(doctor.getAddress())
            .degree(doctor.getDegree())
            .departmentId(doctor.getDepartment() != null ? doctor.getDepartment().getDepartmentId() : null)
            .image(doctor.getImage())
            .build()).toList();

            return ResponseEntity.ok(doctors);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity createDoctor(@RequestBody DoctorModel doctor) {
        try{
            Doctor newDoctor = new Doctor();
            newDoctor.setFullName(doctor.getFullName());
            newDoctor.setDateOfBirth(doctor.getDateOfBirth());
            newDoctor.setGender(doctor.getGender());
            newDoctor.setPhone(doctor.getPhone());
            newDoctor.setAddress(doctor.getAddress());
            newDoctor.setDegree(doctor.getDegree());
            if(doctor.getImage() != null && !doctor.getImage().isEmpty()){
                newDoctor.setImage(doctor.getImage());
            }
            if(doctor.getDepartmentId() != null) {
                Department department = departmentService.findById(doctor.getDepartmentId());
                newDoctor.setDepartment(department);
            }
            Doctor doctor1 = doctorService.createDoctor(newDoctor);
            return ResponseEntity.ok(doctor1);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateDoctor(@PathVariable Long id, @RequestBody DoctorModel doctor) {
        try {
            Doctor existingDoctor = new Doctor();
            existingDoctor.setFullName(doctor.getFullName());
            existingDoctor.setDateOfBirth(doctor.getDateOfBirth());
            existingDoctor.setGender(doctor.getGender());
            existingDoctor.setPhone(doctor.getPhone());
            existingDoctor.setAddress(doctor.getAddress());
            existingDoctor.setDegree(doctor.getDegree());
            if(doctor.getImage() != null && !doctor.getImage().isEmpty()){
                existingDoctor.setImage(doctor.getImage());
            }
            if(doctor.getDepartmentId() != null) {
                Department department = departmentService.findById(doctor.getDepartmentId());
                existingDoctor.setDepartment(department);
            }
            Doctor doctor1 = doctorService.updateDoctor(id, existingDoctor);
            return ResponseEntity.ok(doctor1);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDoctor(@PathVariable Long id) {
        try{
            doctorService.deleteDoctor(id);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
