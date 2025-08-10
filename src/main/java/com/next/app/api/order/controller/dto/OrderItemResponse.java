package com.next.app.api.order.controller.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;

    public OrderItemResponse(Long productId, String productName, int quantity,
                             BigDecimal price, BigDecimal lineTotal) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.lineTotal = lineTotal;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
