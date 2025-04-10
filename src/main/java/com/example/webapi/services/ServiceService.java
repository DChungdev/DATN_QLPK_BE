package com.example.webapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.webapi.models.entities.ServiceEntity;
import com.example.webapi.repositories.ServiceRepository;

@Service
public class ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;
    
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public ServiceEntity getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public ServiceEntity createService(ServiceEntity service) {
        return serviceRepository.save(service);
    }

    public ServiceEntity updateService(Long id, ServiceEntity service) {
        ServiceEntity existingService = getServiceById(id);
        existingService.setName(service.getName());
        existingService.setPrice(service.getPrice());
        return serviceRepository.save(existingService);
    }
    
    public void deleteService(Long id) {
        ServiceEntity existingService = getServiceById(id);
        serviceRepository.delete(existingService);
    }
}
