package com.example.webapi.services;

import com.example.webapi.models.entities.Department;
import com.example.webapi.models.entities.Doctor;
import com.example.webapi.models.entities.Patient;
import com.example.webapi.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id = " + id));
        return doctor;
    }

    public Doctor getDoctorByUsername(String username) {
        return doctorRepository.findByUsername(username).orElse(null);
    }

    public List<Doctor> getDoctorsByDepartmentId(Long id) {
        return doctorRepository.findByDepartmentId(id);
    }

    public Doctor createDoctor(Doctor doctor) {
        Doctor savedDoctor = doctorRepository.save(doctor);
        return savedDoctor;
    }

    public Doctor updateDoctor(Long id,Doctor doctor) {
        Doctor existingDoctor = findById(id);
        existingDoctor.setFullName(doctor.getFullName());
        existingDoctor.setAddress(doctor.getAddress());
        existingDoctor.setPhone(doctor.getPhone());
        existingDoctor.setGender(doctor.getGender());
        existingDoctor.setDateOfBirth(doctor.getDateOfBirth());
        existingDoctor.setDegree(doctor.getDegree());
        existingDoctor.setDepartment(doctor.getDepartment());
        if(doctor.getImage() != null && !doctor.getImage().isEmpty()){
            // Delete old image if exists
            if (existingDoctor.getImage() != null && !existingDoctor.getImage().isEmpty()) {
                java.io.File oldImage = new java.io.File("./uploads/" + existingDoctor.getImage());
                if (oldImage.exists()) {
                    oldImage.delete();
                }
            }
            existingDoctor.setImage(doctor.getImage());
        }

        return doctorRepository.save(existingDoctor);
    }

    public void deleteDoctor(Long id) {
        Doctor existingDoctor = findById(id);
        doctorRepository.delete(existingDoctor);
    }
}
