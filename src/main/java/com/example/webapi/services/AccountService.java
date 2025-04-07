package com.example.webapi.services;

import com.example.webapi.models.entities.Account;
import com.example.webapi.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Account register(String username, String password, String fullName, String email, String phone, String role) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        Account account = new Account();
        account.setUsername(username);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setFullName(fullName);
        account.setEmail(email);
        account.setPhone(phone);
        account.setRole(role != null ? role : "USER"); // Default role is USER if not specified

        return accountRepository.save(account);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        Account account = findByUsername(username);
        if (!passwordEncoder.matches(oldPassword, account.getPasswordHash())) {
            throw new RuntimeException("Old password does not match");
        }
        account.setPasswordHash(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void resetPassword(String username, String password) {
        Account account = findByUsername(username);
        account.setPasswordHash(passwordEncoder.encode(password));
        accountRepository.save(account);
    }

    public void deleteAccount(String username) {
        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        accountRepository.delete(account);
    }
} 