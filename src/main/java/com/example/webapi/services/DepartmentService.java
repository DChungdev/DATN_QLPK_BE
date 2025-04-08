package com.example.webapi.services;

import com.example.webapi.models.entities.Department;
import com.example.webapi.repositories.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Department findById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id = " + id));
        return department;
    }

    public Department create(Department department) {
        return departmentRepository.save(department);
    }

    public Department update(Long id, Department department) {
        Department existingDepartment = findById(id);
        existingDepartment.setName(department.getName());
        existingDepartment.setDescription(department.getDescription());
        return departmentRepository.save(existingDepartment);
    }

    public void delete(Long id) {
        Department existingDepartment = findById(id);
        departmentRepository.delete(existingDepartment);
    }
}
