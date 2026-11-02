package com.example.online_courier.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CourierDTO {
    private Long id;
    private String courierId;
    private String senderName;
    private String senderCity;
    private String senderMobile;
    private String receiverName;
    private String receiverCity;
    private String recipientMobile;
    private BigDecimal weight;
    private BigDecimal deliveryCharge;
    private BigDecimal calculatedPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<CourierLogDTO> logs;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourierId() { return courierId; }
    public void setCourierId(String courierId) { this.courierId = courierId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderCity() { return senderCity; }
    public void setSenderCity(String senderCity) { this.senderCity = senderCity; }

    public String getSenderMobile() { return senderMobile; }
    public void setSenderMobile(String senderMobile) { this.senderMobile = senderMobile; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverCity() { return receiverCity; }
    public void setReceiverCity(String receiverCity) { this.receiverCity = receiverCity; }

    public String getRecipientMobile() { return recipientMobile; }
    public void setRecipientMobile(String recipientMobile) { this.recipientMobile = recipientMobile; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(BigDecimal deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public BigDecimal getCalculatedPrice() { return calculatedPrice; }
    public void setCalculatedPrice(BigDecimal calculatedPrice) { this.calculatedPrice = calculatedPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<CourierLogDTO> getLogs() { return logs; }
    public void setLogs(List<CourierLogDTO> logs) { this.logs = logs; }
}
