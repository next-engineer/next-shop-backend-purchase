package com.next.app.api.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(name = "OrderResponse")
public class OrderResponse {

    @Schema(description = "주문 ID", example = "1")
    private final Long orderId;

    @Schema(description = "사용자 ID", example = "7")
    private final Long userId;

    @Schema(description = "배송지", example = "서울시 강남구 테헤란로 123")
    private final String deliveryAddress;

    @Schema(description = "주문 상태", example = "CREATED")
    private final String status;

    @Schema(description = "총 금액", example = "398000")
    private final BigDecimal totalPrice;

    @Schema(description = "생성 시각", example = "2025-08-10T21:15:30")
    private final LocalDateTime createdAt;

    @Schema(description = "주문 상품 목록")
    private final List<OrderItemResponse> items;
}
