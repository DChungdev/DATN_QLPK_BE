package com.example.webapi.models.dto;

import java.util.List;

import lombok.Data;

@Data
public class AddServicesRequest {
    private List<Long> serviceIds;
}

