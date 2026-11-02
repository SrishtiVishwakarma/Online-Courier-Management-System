package com.example.online_courier.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("Generating password hashes...\n");
        
        // Generate hash for "admin"
        String adminPassword = "admin";
        String adminHash = encoder.encode(adminPassword);
        System.out.println("Password: " + adminPassword);
        System.out.println("Hash: " + adminHash);
        System.out.println("Verify: " + encoder.matches(adminPassword, adminHash));
        System.out.println();
        
        // Generate hash for "Admin@123"
        String userPassword = "Admin@123";
        String userHash = encoder.encode(userPassword);
        System.out.println("Password: " + userPassword);
        System.out.println("Hash: " + userHash);
        System.out.println("Verify: " + encoder.matches(userPassword, userHash));
        System.out.println();
        
        // Test the old hash from sample_data.sql
        String oldAdminHash = "$2a$10$X5wFuJKqFJLEbr1qA.6qOOgJqS9UdPPDveGVJJLSKdkPf.Io7CzFO";
        System.out.println("Testing old hash from sample_data.sql:");
        System.out.println("Old hash matches 'admin': " + encoder.matches("admin", oldAdminHash));
        System.out.println("Old hash matches 'Admin': " + encoder.matches("Admin", oldAdminHash));
    }
}
