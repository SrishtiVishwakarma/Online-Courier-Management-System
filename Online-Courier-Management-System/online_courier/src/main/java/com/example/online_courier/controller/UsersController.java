package com.example.online_courier.controller;

import com.example.online_courier.dto.CourierDTO;
import com.example.online_courier.service.CourierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final CourierService courierService;

    public UsersController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping("/{userId}/couriers")
    public ResponseEntity<List<CourierDTO>> getUserCouriers(@PathVariable Long userId) {
        return ResponseEntity.ok(courierService.getUserCouriers(userId));
    }
}
