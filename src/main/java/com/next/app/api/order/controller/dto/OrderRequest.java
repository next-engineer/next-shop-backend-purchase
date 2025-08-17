package com.next.app.api.order.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {

    private String delivery_address;  // 고객 입력 배송지, 선택적 필드

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
