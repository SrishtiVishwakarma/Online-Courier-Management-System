package com.example.online_courier.controller;

import com.example.online_courier.dto.LoginRequest;
import com.example.online_courier.dto.LoginResponse;
import com.example.online_courier.dto.RegisterRequest;
import com.example.online_courier.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        authService.registerUser(request.getName(), request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully",
            "status", "success"
        ));
    }
}
