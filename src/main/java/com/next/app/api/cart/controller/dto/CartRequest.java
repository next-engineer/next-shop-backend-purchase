package com.next.app.api.cart.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니 상품 추가/수정 요청 DTO
 */
@Getter
@Setter
public class CartRequest {
    @NotNull
    private Long productId; // 상품 ID

    @Min(1)
    private int quantity;   // 수량
}
