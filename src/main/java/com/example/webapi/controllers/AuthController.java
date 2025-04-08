package com.example.webapi.controllers;

import com.example.webapi.common.JwtUtil;
import com.example.webapi.models.dto.*;
import com.example.webapi.models.entities.Account;
import com.example.webapi.models.entities.Doctor;
import com.example.webapi.models.entities.Patient;
import com.example.webapi.models.entities.RefreshToken;
import com.example.webapi.security.CustomUserDetailsService;
import com.example.webapi.services.AccountService;
import com.example.webapi.services.DoctorService;
import com.example.webapi.services.PatientService;
import com.example.webapi.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountService accountService;
    @Autowired
    private PatientService patientService;
    @Autowired
    DoctorService doctorService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Account account = accountService.register(
                request.getUsername(),
                request.getPassword(),
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getRole()
            );
            if(request.getRole() != null && request.getRole().equalsIgnoreCase("doctor")) {
                Doctor doctor = new Doctor();
                doctor.setAccount(account);
                doctor.setFullName(request.getFullName());
                doctor.setPhone(request.getPhone());
                doctorService.createDoctor(doctor);
            }
            else{
                Patient patient = new Patient();
                patient.setAccount(account);
                patient.setFullName(request.getFullName());
                patient.setPhone(request.getPhone());
                Patient created = patientService.createPatient(patient);
            }
            return ResponseEntity.ok("Account registered successfully");


//            // Auto login after registration
//            Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
//            );
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String accessToken = jwtUtil.generateAccessToken(userDetails);
//            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
//
//            // Save refresh token
//            refreshTokenService.createRefreshToken(request.getUsername(), refreshToken);
//
//            return ResponseEntity.ok(new AuthResponse(
//                accessToken,
//                refreshToken,
//                account.getRole(),
//                account.getUsername(),
//                account.getFullName()
//            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Save refresh token
            refreshTokenService.createRefreshToken(request.getUsername(), refreshToken);

            Account account = accountService.findByUsername(request.getUsername());

            return ResponseEntity.ok(new AuthResponse(
                accessToken,
                refreshToken,
                account.getRole(),
                account.getUsername(),
                account.getFullName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
            Account account = accountService.findByUsername(refreshToken.getUsername());
            UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(
                newAccessToken,
                request.getRefreshToken(),
                account.getRole(),
                userDetails.getUsername(),
                account.getFullName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<?> revokeToken(@RequestBody RefreshTokenRequest request) {
        try {
            refreshTokenService.revokeRefreshToken(request.getRefreshToken());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping  ("/delete/{username}")
    public ResponseEntity deleteAccount(@PathVariable String username){
        try{
            Patient patient = patientService.getPatientByUsername(username);
            Doctor doctor = doctorService.getDoctorByUsername(username);
            if(patient != null){
                patient.setAccount(null);
                patientService.updatePatient(patient.getPatientId(), patient);
                return ResponseEntity.ok("Account deleted successfully");
            } else if (doctor != null) {
                doctor.setAccount(null);
                doctorService.updateDoctor(doctor.getDoctorId(), doctor);
                return ResponseEntity.ok("Account deleted successfully");
            }
            else{
                accountService.deleteAccount(username);
                return ResponseEntity.ok("Account deleted successfully");
            }
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            accountService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try{
            accountService.resetPassword(request.getUsername(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAccounts() {
        try{
            List<Account> accounts = accountService.getAllAccounts();
            return ResponseEntity.ok(accounts);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 