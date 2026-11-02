package com.example.online_courier.repository;

import com.example.online_courier.entity.StaffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StaffRequestRepository extends JpaRepository<StaffRequest, Long> {
    List<StaffRequest> findByStatus(StaffRequest.RequestStatus status);
}
