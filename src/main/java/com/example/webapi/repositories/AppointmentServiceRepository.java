package com.example.webapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.webapi.models.entities.AppointmentServices;

public interface AppointmentServiceRepository extends JpaRepository<AppointmentServices, Long> {
    
}
