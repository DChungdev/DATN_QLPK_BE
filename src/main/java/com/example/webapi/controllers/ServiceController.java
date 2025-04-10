package com.example.webapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.webapi.models.entities.ServiceEntity;
import com.example.webapi.services.ServiceService;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity getAllServices() {
        try {
            List<ServiceEntity> services = serviceService.getAllServices();
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getServiceById(@PathVariable Long id) {
        try {
            ServiceEntity service = serviceService.getServiceById(id);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity createService(@RequestBody ServiceEntity service) {
        try {
            ServiceEntity createdService = serviceService.createService(service);
            return ResponseEntity.ok(createdService);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateService(@PathVariable Long id, @RequestBody ServiceEntity service) {
        try {
            ServiceEntity updatedService = serviceService.updateService(id, service);
            return ResponseEntity.ok(updatedService);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
