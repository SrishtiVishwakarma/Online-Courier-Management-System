package com.example.online_courier.service;

import com.example.online_courier.dto.CourierDTO;
import com.example.online_courier.dto.CourierLogDTO;
import com.example.online_courier.dto.CourierRequest;
import com.example.online_courier.entity.Courier;
import com.example.online_courier.entity.CourierLog;
import com.example.online_courier.entity.User;
import com.example.online_courier.repository.CourierLogRepository;
import com.example.online_courier.repository.CourierRepository;
import com.example.online_courier.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourierService {

    private final CourierRepository courierRepository;
    private final CourierLogRepository courierLogRepository;
    private final UserRepository userRepository;

    public CourierService(CourierRepository courierRepository, CourierLogRepository courierLogRepository, UserRepository userRepository) {
        this.courierRepository = courierRepository;
        this.courierLogRepository = courierLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, String> createCourier(CourierRequest request, String userEmail) {
        User sender = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Courier courier = new Courier();
        String courierId = generateCourierId();
        courier.setCourierId(courierId);
        courier.setTrackingNumber(courierId); // Set tracking number same as courier ID
        courier.setUserId(sender.getId()); // Set user_id to sender's ID
        courier.setSender(sender);
        courier.setSenderName(request.getSenderName());
        courier.setSenderAddress(request.getSenderAddress());
        courier.setSenderCity(request.getSenderCity());
        courier.setSenderMobile(request.getSenderMobile());
        courier.setPickupAddress(request.getSenderAddress()); // Set pickup same as sender address
        courier.setPickupCity(request.getSenderCity()); // Set pickup city same as sender city
        courier.setPickupPincode("000000"); // Default pickup pincode
        courier.setReceiverName(request.getReceiverName());
        courier.setReceiverAddress(request.getReceiverAddress());
        courier.setReceiverCity(request.getReceiverCity());
        courier.setRecipientMobile(request.getRecipientMobile());
        courier.setDestinationAddress(request.getReceiverAddress()); // Set destination same as receiver
        courier.setDestinationCity(request.getReceiverCity()); // Set destination city same as receiver city
        courier.setDestinationPincode("000000"); // Default pincode (field not in request yet)
        courier.setWeight(request.getWeight());
        courier.setNotes(request.getNotes());
        courier.setStatus(Courier.CourierStatus.CREATED);
        
        // Calculate delivery charge: ₹30 per kg, rounded up
        // e.g., 1.2kg becomes 2kg for charging purposes
        java.math.BigDecimal chargePerKg = new java.math.BigDecimal("30");
        long roundedWeight = (long) Math.ceil(request.getWeight().doubleValue());
        java.math.BigDecimal deliveryCharge = chargePerKg.multiply(java.math.BigDecimal.valueOf(roundedWeight));
        courier.setDeliveryCharge(deliveryCharge);
        courier.setCalculatedPrice(deliveryCharge); // Set calculated_price (same as delivery_charge)

        courier = courierRepository.save(courier);

        // Create log entry
        createLog(courier, Courier.CourierStatus.CREATED, null, "Courier created by user");

        return Map.of("courier_id", courier.getCourierId(), "courierId", courier.getCourierId());
    }

    public CourierDTO getCourierByCode(String courierId) {
        Courier courier = courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        return mapToDTO(courier);
    }

    public List<CourierDTO> getUserCouriers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return courierRepository.findBySender(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    //it's for staff to see available pickups for their city
    public List<CourierDTO> getAvailablePickupsForCity(String city) {
        return courierRepository.findBySenderCityAndStatus(city, Courier.CourierStatus.CREATED).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignCourier(String courierId, String staffEmail) {
        Courier courier = courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Validate city match
        if (!courier.getSenderCity().equals(staff.getCity())) {
            throw new RuntimeException("Staff can only handle couriers from their assigned city");
        }

        courier.setAssignedStaff(staff);
        courierRepository.save(courier);
        createLog(courier, courier.getStatus(), staff, "Assigned to staff");
    }

    @Transactional
    public void markPickedUp(String courierId, String staffEmail) {
        Courier courier = courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Validate city match
        if (!courier.getSenderCity().equals(staff.getCity())) {
            throw new RuntimeException("Staff can only pick up couriers from their assigned city");
        }

        courier.setStatus(Courier.CourierStatus.IN_TRANSIT);
        courier.setAssignedStaff(null); // Clear assignment so destination city staff can handle it
        courierRepository.save(courier);
        createLog(courier, Courier.CourierStatus.PICKED_UP, staff, "Picked up by staff");
        createLog(courier, Courier.CourierStatus.IN_TRANSIT, staff, "In transit to destination city");
    }

    @Transactional
    public void markDelivered(String courierId, String staffEmail) {
        Courier courier = courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Validate courier is in transit and destination city matches staff city
        if (courier.getStatus() != Courier.CourierStatus.IN_TRANSIT) {
            throw new RuntimeException("Courier is not in transit");
        }
        
        if (!courier.getReceiverCity().equals(staff.getCity())) {
            throw new RuntimeException("Staff can only deliver couriers to their assigned city");
        }

        courier.setAssignedStaff(staff); // Assign to delivery staff for tracking
        courier.setStatus(Courier.CourierStatus.DELIVERED);
        courierRepository.save(courier);
        createLog(courier, Courier.CourierStatus.DELIVERED, staff, "Delivered by staff");
    }

    public List<CourierDTO> getStaffDeliveries(String staffEmail) {
        User staff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Get couriers that need to be delivered in this staff's city
        // Only staff whose city matches the receiver's city can see these deliveries
        // IN_TRANSIT couriers have no assigned staff (cleared during pickup)
        return courierRepository.findByReceiverCityAndStatus(staff.getCity(), Courier.CourierStatus.IN_TRANSIT).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getStatusSummary() {
        List<Courier> allCouriers = courierRepository.findAll();
        return allCouriers.stream()
                .collect(Collectors.groupingBy(c -> c.getStatus().name(), Collectors.counting()));
    }

    private void createLog(Courier courier, Courier.CourierStatus status, User staff, String note) {
        CourierLog log = new CourierLog();
        log.setCourier(courier);
        log.setStatus(status);
        log.setStaff(staff);
        log.setNote(note);
        courierLogRepository.save(log);
    }

    private CourierDTO mapToDTO(Courier courier) {
        CourierDTO dto = new CourierDTO();
        dto.setId(courier.getId());
        dto.setCourierId(courier.getCourierId());
        dto.setSenderName(courier.getSenderName());
        dto.setSenderCity(courier.getSenderCity());
        dto.setSenderMobile(courier.getSenderMobile());
        dto.setReceiverName(courier.getReceiverName());
        dto.setReceiverCity(courier.getReceiverCity());
        dto.setRecipientMobile(courier.getRecipientMobile());
        dto.setWeight(courier.getWeight());
        
        // Calculate delivery charge if null (for backward compatibility)
        java.math.BigDecimal deliveryCharge = courier.getDeliveryCharge();
        java.math.BigDecimal calculatedPrice = courier.getCalculatedPrice();
        if (deliveryCharge == null && courier.getWeight() != null) {
            java.math.BigDecimal chargePerKg = new java.math.BigDecimal("30");
            long roundedWeight = (long) Math.ceil(courier.getWeight().doubleValue());
            deliveryCharge = chargePerKg.multiply(java.math.BigDecimal.valueOf(roundedWeight));
            calculatedPrice = deliveryCharge; // Set calculated price same as delivery charge
        }
        dto.setDeliveryCharge(deliveryCharge);
        dto.setCalculatedPrice(calculatedPrice);
        
        dto.setStatus(courier.getStatus().name());
        dto.setCreatedAt(courier.getCreatedAt());

        List<CourierLogDTO> logs = courierLogRepository.findByCourierOrderByTimestampAsc(courier).stream()
                .map(log -> {
                    CourierLogDTO logDTO = new CourierLogDTO();
                    logDTO.setStatus(log.getStatus().name());
                    logDTO.setNote(log.getNote());
                    logDTO.setTimestamp(log.getTimestamp());
                    logDTO.setCourierId(courier.getCourierId());
                    return logDTO;
                })
                .collect(Collectors.toList());
        dto.setLogs(logs);

        return dto;
    }

    private String generateCourierId() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); //datePart is the current date in the format yyyyMMdd
        long count = courierRepository.count() + 1;
        return String.format("OCMS-%s-%05d", datePart, count);
        
    }
}
