package com.example.online_courier.controller;

import com.example.online_courier.dto.CourierDTO;
import com.example.online_courier.dto.UserDTO;
import com.example.online_courier.entity.StaffRequest;
import com.example.online_courier.entity.User;
import com.example.online_courier.entity.Courier;
import com.example.online_courier.service.AdminService;
import com.example.online_courier.service.CourierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final CourierService courierService;

    public AdminController(AdminService adminService, CourierService courierService) {
        this.adminService = adminService;
        this.courierService = courierService;
    }

    @GetMapping("/staff-requests")
    public ResponseEntity<List<Map<String, Object>>> getPendingStaffRequests() {
        List<StaffRequest> requests = adminService.getPendingStaffRequests();
        List<Map<String, Object>> response = requests.stream()
                .map(r -> Map.of(
                    "id", (Object) r.getId(),
                    "name", r.getName(),
                    "email", r.getEmail(),
                    "city", r.getCity(),
                    "status", r.getStatus().name()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/staff-requests/{id}/approve")
    public ResponseEntity<Map<String, String>> approveStaffRequest(@PathVariable Long id) {
        adminService.approveStaffRequest(id);
        return ResponseEntity.ok(Map.of(
            "message", "Staff request approved successfully",
            "status", "success"
        ));
    }

    @PostMapping("/staff-requests/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectStaffRequest(@PathVariable Long id) {
        adminService.rejectStaffRequest(id);
        return ResponseEntity.ok(Map.of(
            "message", "Staff request rejected successfully",
            "status", "success"
        ));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<UserDTO>> getAllStaff() {
        List<User> staff = adminService.getAllStaff();
        List<UserDTO> staffDTOs = staff.stream()
                .map(s -> new UserDTO(s.getId(), s.getName(), s.getEmail(), s.getRole().name(), s.getCity(), s.getStatus().name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(staffDTOs);
    }

    @GetMapping("/dashboard/status-summary")
    public ResponseEntity<Map<String, Long>> getStatusSummary() {
        return ResponseEntity.ok(courierService.getStatusSummary());
    }

    @GetMapping("/couriers/search")
    public ResponseEntity<CourierDTO> searchCourier(@RequestParam("courier_id") String courierId) {
        return ResponseEntity.ok(courierService.getCourierByCode(courierId));
    }

    @GetMapping("/couriers")
    public ResponseEntity<List<Map<String, Object>>> getAllCouriers() {
        List<Courier> couriers = adminService.getAllCouriers();
        List<Map<String, Object>> response = couriers.stream()
                .map(c -> {
                    Map<String, Object> courierMap = new java.util.HashMap<>();
                    courierMap.put("id", c.getId());
                    courierMap.put("courierId", c.getCourierId() != null ? c.getCourierId() : "N/A");
                    courierMap.put("trackingNumber", c.getTrackingNumber() != null ? c.getTrackingNumber() : "N/A");
                    courierMap.put("senderName", c.getSenderName() != null ? c.getSenderName() : "N/A");
                    courierMap.put("receiverName", c.getReceiverName() != null ? c.getReceiverName() : "N/A");
                    courierMap.put("senderCity", c.getSenderCity() != null ? c.getSenderCity() : "N/A");
                    courierMap.put("receiverCity", c.getReceiverCity() != null ? c.getReceiverCity() : "N/A");
                    courierMap.put("status", c.getStatus() != null ? c.getStatus().name() : "UNKNOWN");
                    courierMap.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : "N/A");
                    courierMap.put("assignedStaff", c.getAssignedStaff() != null ? c.getAssignedStaff().getName() : "Not Assigned");
                    return courierMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
