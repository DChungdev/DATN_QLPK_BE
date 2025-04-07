package com.example.webapi.controllers;

import com.example.webapi.models.entities.Doctor;
import com.example.webapi.repositories.DoctorRepository;
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

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping
    public List<Doctor> getDoctors() {
        return doctorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity getDoctor(@PathVariable Long id) {
        try{
            Doctor doctor = doctorService.findById(id);
            return ResponseEntity.ok(doctor);
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
                return ResponseEntity.ok(doctor);
            }
            else return ResponseEntity.notFound().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity createDoctor(@RequestBody Doctor doctor) {
        try{
            Doctor doctor1 = doctorService.createDoctor(doctor);
            return ResponseEntity.ok(doctor1);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        try {
            Doctor doctor1 = doctorService.updateDoctor(id, doctor);
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
