package com.next.app.api.cart.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 장바구니 아이템 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class CartItemResponse {
    private final Long productId;     // 상품 ID
    private final String productName; // 상품명
    private final int quantity;       // 수량
    private final BigDecimal price;   // 단가
    private final BigDecimal lineTotal;// 총금액
}
