package com.example.online_courier.controller;

import com.example.online_courier.dto.CourierDTO;
import com.example.online_courier.dto.CourierLogDTO;
import com.example.online_courier.dto.StaffRequestDTO;
import com.example.online_courier.entity.CourierLog;
import com.example.online_courier.entity.StaffRequest;
import com.example.online_courier.entity.User;
import com.example.online_courier.repository.CourierLogRepository;
import com.example.online_courier.repository.StaffRequestRepository;
import com.example.online_courier.repository.UserRepository;
import com.example.online_courier.service.CourierService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final CourierService courierService;
    private final UserRepository userRepository;
    private final CourierLogRepository courierLogRepository;
    private final StaffRequestRepository staffRequestRepository;

    public StaffController(CourierService courierService, UserRepository userRepository,
                          CourierLogRepository courierLogRepository, StaffRequestRepository staffRequestRepository) {
        this.courierService = courierService;
        this.userRepository = userRepository;
        this.courierLogRepository = courierLogRepository;
        this.staffRequestRepository = staffRequestRepository;
    }

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> submitStaffRequest(@RequestBody StaffRequestDTO request) {
        StaffRequest staffRequest = new StaffRequest();
        staffRequest.setName(request.getName());
        staffRequest.setEmail(request.getEmail());
        staffRequest.setPassword(request.getPassword()); // Set the password from request
        staffRequest.setCity(request.getCity());
        staffRequest.setStatus(StaffRequest.RequestStatus.PENDING);
        staffRequestRepository.save(staffRequest);
        return ResponseEntity.ok(Map.of("message", "Staff request submitted successfully"));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getSummary(Authentication authentication) {
        User staff = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        long pendingPickups = courierService.getAvailablePickupsForCity(staff.getCity()).size();
        long deliveries = courierService.getStaffDeliveries(authentication.getName()).size();

        return ResponseEntity.ok(Map.of("pendingPickups", pendingPickups, "deliveries", deliveries));
    }

    @GetMapping("/available-pickups")
    public ResponseEntity<List<CourierDTO>> getAvailablePickups(Authentication authentication) {
        User staff = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        return ResponseEntity.ok(courierService.getAvailablePickupsForCity(staff.getCity()));
    }

    @PostMapping("/couriers/{courierId}/assign")
    public ResponseEntity<Map<String, String>> assignCourier(@PathVariable String courierId, Authentication authentication) {
        courierService.assignCourier(courierId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Courier assigned successfully"));
    }

    @PostMapping("/couriers/{courierId}/pickup")
    public ResponseEntity<Map<String, String>> markPickedUp(@PathVariable String courierId, Authentication authentication) {
        courierService.markPickedUp(courierId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Courier marked as picked up successfully"));
    }

    @GetMapping("/deliveries")
    public ResponseEntity<List<CourierDTO>> getDeliveries(Authentication authentication) {
        return ResponseEntity.ok(courierService.getStaffDeliveries(authentication.getName()));
    }

    @PostMapping("/couriers/{courierId}/deliver")
    public ResponseEntity<Map<String, String>> markDelivered(@PathVariable String courierId, Authentication authentication) {
        courierService.markDelivered(courierId, authentication.getName());
        return ResponseEntity.ok(Map.of("message", "Courier marked as delivered successfully"));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<CourierLogDTO>> getLogs(Authentication authentication) {
        User staff = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        List<CourierLog> logs = courierLogRepository.findByStaff(staff);
        List<CourierLogDTO> logDTOs = logs.stream()
                .map(log -> {
                    CourierLogDTO dto = new CourierLogDTO();
                    dto.setStatus(log.getStatus().name());
                    dto.setNote(log.getNote());
                    dto.setTimestamp(log.getTimestamp());
                    dto.setCourierId(log.getCourier().getCourierId());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(logDTOs);
    }
}
