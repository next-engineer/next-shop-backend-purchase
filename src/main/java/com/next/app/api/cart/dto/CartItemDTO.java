package com.next.app.api.cart.dto;

public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;

    public CartItemDTO(Long id, Long productId, String productName, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    // getter, setter 생략 (필요하면 lombok @Data 사용해도 됨)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
