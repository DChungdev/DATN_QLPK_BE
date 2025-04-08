package com.example.webapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DoAnApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoAnApplication.class, args);
	}

}
