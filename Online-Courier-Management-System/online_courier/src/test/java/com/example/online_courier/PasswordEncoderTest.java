package com.example.online_courier;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Generate hashes for testing
        System.out.println("=== Password Hash Generator ===");
        System.out.println("Password 'admin' hash: " + encoder.encode("admin"));
        System.out.println("Password 'Admin@123' hash: " + encoder.encode("Admin@123"));
        
        // Test existing hashes
        System.out.println("\n=== Testing Existing Hashes ===");
        
        String adminHash = "$2a$10$X5wFuJKqFJLEbr1qA.6qOOgJqS9UdPPDveGVJJLSKdkPf.Io7CzFO";
        String userHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye1J5hq0BREkS36vCCy8.9XjwGYvb7vv6";
        
        System.out.println("Hash for 'admin' matches 'admin': " + encoder.matches("admin", adminHash));
        System.out.println("Hash for 'Admin@123' matches user hash: " + encoder.matches("Admin@123", userHash));
        
        // Additional tests
        System.out.println("\n=== Additional Password Tests ===");
        System.out.println("Test 'admin' vs 'Admin': " + encoder.matches("Admin", adminHash));
        System.out.println("Test 'admin ' (with space) vs admin hash: " + encoder.matches("admin ", adminHash));
        System.out.println("Test 'admin@123' vs user hash: " + encoder.matches("admin@123", userHash));
    }
}
