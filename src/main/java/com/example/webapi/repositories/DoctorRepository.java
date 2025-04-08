package com.example.webapi.repositories;

import com.example.webapi.models.entities.Department;
import com.example.webapi.models.entities.Doctor;
import com.example.webapi.models.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("SELECT p FROM Doctor p JOIN p.account a WHERE a.username = :username")
    Optional<Doctor> findByUsername(@Param("username") String username);

    @Query("SELECT d FROM Doctor d JOIN d.department dept WHERE dept.departmentId = :departmentId")
    List<Doctor> findByDepartmentId(@Param("departmentId") Long departmentId);
}
