package com.example.online_courier.controller;

import com.example.online_courier.dto.CourierDTO;
import com.example.online_courier.dto.CourierRequest;
import com.example.online_courier.entity.Courier;
import com.example.online_courier.repository.CourierRepository;
import com.example.online_courier.service.CourierService;
import com.example.online_courier.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/couriers")
public class CourierController {

    private final CourierService courierService;
    private final CourierRepository courierRepository;
    private final PdfService pdfService;

    public CourierController(CourierService courierService, CourierRepository courierRepository, PdfService pdfService) {
        this.courierService = courierService;
        this.courierRepository = courierRepository;
        this.pdfService = pdfService;
    }
    // it's for user to create a new courier
    @PostMapping
    public ResponseEntity<Map<String, String>> createCourier(@RequestBody CourierRequest request, Authentication authentication) {
        return ResponseEntity.ok(courierService.createCourier(request, authentication.getName()));
    }
    //it's for staff to see available pickups for their city
    @GetMapping("/{courierId}")
    public ResponseEntity<CourierDTO> getCourier(@PathVariable String courierId) {
        return ResponseEntity.ok(courierService.getCourierByCode(courierId));
    }
    
    @GetMapping("/{courierId}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable String courierId) {
        Courier courier = courierRepository.findByCourierId(courierId)
                .orElseThrow(() -> new RuntimeException("Courier not found"));

        byte[] pdf = pdfService.generateCourierReceipt(courier);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt-" + courierId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
