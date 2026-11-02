package com.example.online_courier.config;

import com.example.online_courier.entity.User;
import com.example.online_courier.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin user already exists
            if (!userRepository.findByEmail("admin@ocms.com").isPresent()) {
                User admin = new User();
                admin.setName("Admin User");
                admin.setUsername("admin");
                admin.setEmail("admin@ocms.com");
                admin.setPassword(passwordEncoder.encode("ocms@123"));
                admin.setRole(User.Role.ADMIN);
                admin.setStatus(User.UserStatus.ACTIVE);
                userRepository.save(admin);
            }

            // Create sample staff users if they don't exist
            if (!userRepository.findByEmail("staff.mumbai@ocms.com").isPresent()) {
                User staffMumbai = new User();
                staffMumbai.setName("Sanjay Staff Mumbai");
                staffMumbai.setUsername("staff.mumbai");
                staffMumbai.setEmail("staff.mumbai@ocms.com");
                staffMumbai.setPassword(passwordEncoder.encode("ocms@123"));
                staffMumbai.setRole(User.Role.STAFF);
                staffMumbai.setCity("Mumbai");
                staffMumbai.setStatus(User.UserStatus.ACTIVE);
                userRepository.save(staffMumbai);
            }

            if (!userRepository.findByEmail("staff.delhi@ocms.com").isPresent()) {
                User staffDelhi = new User();
                staffDelhi.setName("Neha Staff Delhi");
                staffDelhi.setUsername("staff.delhi");
                staffDelhi.setEmail("staff.delhi@ocms.com");
                staffDelhi.setPassword(passwordEncoder.encode("ocms@123"));
                staffDelhi.setRole(User.Role.STAFF);
                staffDelhi.setCity("Delhi");
                staffDelhi.setStatus(User.UserStatus.ACTIVE);
                userRepository.save(staffDelhi);
            }

            // Force override ALL users' passwords to ocms@123 (No console prints)
            List<User> allUsers = userRepository.findAll();
            String newPassword = passwordEncoder.encode("ocms@123");
            
            for (User user : allUsers) {
                user.setPassword(newPassword);
            }
            
            userRepository.saveAll(allUsers);
        };
    }
}
