package com.example.webapi.repositories;

import com.example.webapi.models.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p JOIN p.account a WHERE a.username = :username")
    Optional<Patient> findByUsername(@Param("username") String username);
}
