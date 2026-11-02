package com.example.online_courier.service;

import com.example.online_courier.entity.StaffRequest;
import com.example.online_courier.entity.User;
import com.example.online_courier.entity.Courier;
import com.example.online_courier.repository.StaffRequestRepository;
import com.example.online_courier.repository.UserRepository;
import com.example.online_courier.repository.CourierRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final StaffRequestRepository staffRequestRepository;
    private final UserRepository userRepository;
    private final CourierRepository courierRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(StaffRequestRepository staffRequestRepository, UserRepository userRepository, CourierRepository courierRepository, PasswordEncoder passwordEncoder) {
        this.staffRequestRepository = staffRequestRepository;
        this.userRepository = userRepository;
        this.courierRepository = courierRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<StaffRequest> getPendingStaffRequests() {
        return staffRequestRepository.findByStatus(StaffRequest.RequestStatus.PENDING);
    }

    @Transactional
    public void approveStaffRequest(Long requestId) {
        StaffRequest request = staffRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != StaffRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        // Generate username from email (part before @)
        String username = request.getEmail().split("@")[0];
        
        // Ensure username is unique
        String finalUsername = username;
        int counter = 1;
        while (userRepository.findByUsername(finalUsername).isPresent()) {
            finalUsername = username + counter;
            counter++;
        }

        // Create staff user
        User staff = new User();
        staff.setName(request.getName());
        staff.setUsername(finalUsername);
        staff.setEmail(request.getEmail());
        
        // Handle password - use staff-chosen password or default if null
        String password = request.getPassword();
        if (password == null || password.trim().isEmpty()) {
            // Generate default password: staff123
            password = "staff123";
        }
        staff.setPassword(passwordEncoder.encode(password));
        
        staff.setRole(User.Role.STAFF);
        staff.setCity(request.getCity());
        staff.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(staff);

        // Update request status
        request.setStatus(StaffRequest.RequestStatus.APPROVED);
        staffRequestRepository.save(request);
    }

    @Transactional
    public void rejectStaffRequest(Long requestId) {
        StaffRequest request = staffRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != StaffRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request already processed");
        }

        request.setStatus(StaffRequest.RequestStatus.REJECTED);
        staffRequestRepository.save(request);
    }

    public List<User> getAllStaff() {
        return userRepository.findByRole(User.Role.STAFF);
    }

    public List<Courier> getAllCouriers() {
        return courierRepository.findAll();
    }
}
