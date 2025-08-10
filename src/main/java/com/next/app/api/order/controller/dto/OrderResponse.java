package com.next.app.api.order.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long orderId;
    private Long userId;
    private String deliveryAddress;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    public OrderResponse(Long orderId, Long userId, String deliveryAddress, String status,
                         BigDecimal totalPrice, LocalDateTime createdAt, List<OrderItemResponse> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getStatus() { return status; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItemResponse> getItems() { return items; }
}