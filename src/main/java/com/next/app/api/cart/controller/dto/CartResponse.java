package com.next.app.api.cart.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 장바구니 전체 조회 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class CartResponse {
    private final Long cartId;              // 장바구니 ID
    private final Long userId;              // 사용자 ID
    private final List<CartItemResponse> items; // 상품 아이템 목록
    private final BigDecimal totalPrice;    // 총 금액
    private final LocalDateTime createdAt;  // 생성일시
    private final LocalDateTime updatedAt;  // 수정일시
}
