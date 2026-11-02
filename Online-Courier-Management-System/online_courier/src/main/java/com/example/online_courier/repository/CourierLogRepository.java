package com.example.online_courier.repository;

import com.example.online_courier.entity.Courier;
import com.example.online_courier.entity.CourierLog;
import com.example.online_courier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourierLogRepository extends JpaRepository<CourierLog, Long> {
    List<CourierLog> findByCourier(Courier courier);
    List<CourierLog> findByStaff(User staff);
    List<CourierLog> findByCourierOrderByTimestampAsc(Courier courier);
}
