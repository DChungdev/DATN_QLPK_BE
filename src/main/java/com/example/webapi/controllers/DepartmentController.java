package com.example.webapi.controllers;

import com.example.webapi.models.entities.Department;
import com.example.webapi.services.DepartmentService;
import com.example.webapi.services.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity getAllDepartments() {
        try{
            List<Department> departments = departmentService.findAll();
            return ResponseEntity.ok(departments);
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getDepartmentById(@PathVariable Long id) {
        try{
            Department department = departmentService.findById(id);
            return ResponseEntity.ok(department);
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity createDepartment(@RequestBody Department department) {
        try{
            Department newDepartment = departmentService.create(department);
            return ResponseEntity.ok(newDepartment);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try{
            Department updated = departmentService.update(id, department);
            return ResponseEntity.ok(updated);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDepartment(@PathVariable Long id) {
        try{
            doctorService.getDoctorsByDepartmentId(id).forEach(doctor -> {
                doctor.setDepartment(null);
            });
            departmentService.delete(id);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
