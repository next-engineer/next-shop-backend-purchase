package com.next.app.api.order.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {

    @NotBlank
    private String deliveryAddress;

    @NotEmpty
    private List<Item> items;

    @Data
    public static class Item {
        @NotNull
        private Long productId;

        @Min(1)
        private int quantity;

        @NotNull
        private BigDecimal price;
    }
}
