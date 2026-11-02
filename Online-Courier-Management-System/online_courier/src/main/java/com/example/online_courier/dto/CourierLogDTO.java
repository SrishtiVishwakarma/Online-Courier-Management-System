package com.example.online_courier.dto;

import java.time.LocalDateTime;

public class CourierLogDTO {
    private String status;
    private String note;
    private LocalDateTime timestamp;
    private String courierId;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getCourierId() { return courierId; }
    public void setCourierId(String courierId) { this.courierId = courierId; }
}
