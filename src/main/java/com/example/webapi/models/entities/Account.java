package com.example.webapi.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String email;

    private String phone;

    private String role;

//    // Liên kết 1-1 với Doctor (doctor.account)
//    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private Doctor doctor;
//
//    // Liên kết 1-1 với Patient (patient.account)
//    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private Patient patient;
}
