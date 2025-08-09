package com.next.app.api.order.controller.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private BigDecimal totalPrice;
    private String status;
    private List<Item> items;
    private String deliveryAddress;

    @Data
    public static class Item {
        private Long productId;
        private int quantity;
        private BigDecimal price;
    }
}