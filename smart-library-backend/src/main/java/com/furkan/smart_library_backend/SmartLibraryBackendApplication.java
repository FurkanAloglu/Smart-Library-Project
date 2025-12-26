package com.furkan.smart_library_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SmartLibraryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartLibraryBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner bcryptPrinter() {
		return args -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
			String hash = encoder.encode("123");
			System.out.println("BCrypt hash (123):");
			System.out.println(hash);
		};
	}
}
