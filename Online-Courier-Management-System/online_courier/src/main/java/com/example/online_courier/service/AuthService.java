package com.example.online_courier.service;

import com.example.online_courier.dto.LoginRequest;
import com.example.online_courier.dto.LoginResponse;
import com.example.online_courier.dto.UserDTO;
import com.example.online_courier.entity.User;
import com.example.online_courier.repository.UserRepository;
import com.example.online_courier.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user;
        
        // Check if logging in with admin username
        if ("admin".equalsIgnoreCase(request.getEmail())) {
            user = userRepository.findByEmail("admin@ocms.com")
                    .orElseThrow(() -> new RuntimeException("Admin user not found. Please ensure database is initialized."));
        } else {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("Account is not active. Please contact administrator.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        UserDTO userDTO = new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name(),
            user.getCity(),
            user.getStatus().name()
        );

        return new LoginResponse(token, user.getRole().name(), userDTO);
    }

    public User registerUser(String name, String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.USER);
        user.setStatus(User.UserStatus.ACTIVE);

        return userRepository.save(user);
    }
}
