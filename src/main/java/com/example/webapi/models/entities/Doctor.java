package com.example.webapi.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    // Liên kết đến tài khoản (nếu bác sĩ cũng có account đăng nhập)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

    @Column(nullable = false)
    private String fullName;

    private Date dateOfBirth;
    private String gender;
    private String phone;
    private String address;
    private String degree;
    private String image;
    // Liên kết đến khoa
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true)
//    @JsonIgnore
    private Department department;

    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    private Date updatedAt = new Date();


}
