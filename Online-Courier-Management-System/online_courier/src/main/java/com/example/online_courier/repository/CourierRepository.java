package com.example.online_courier.repository;

import com.example.online_courier.entity.Courier;
import com.example.online_courier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findByCourierId(String courierId);
    List<Courier> findBySender(User sender);
    List<Courier> findByStatus(Courier.CourierStatus status);
    List<Courier> findBySenderCityAndStatus(String city, Courier.CourierStatus status);
    List<Courier> findByReceiverCityAndStatus(String city, Courier.CourierStatus status);
    List<Courier> findByAssignedStaff(User staff);
}
