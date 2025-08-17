package com.next.app.api.order.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(name = "OrderResponse")
public class OrderResponse {

    @Schema(description = "주문 ID")
    private Long orderId;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "배송주소")
    private String deliveryAddress;

    @Schema(description = "주문 상태")
    private String status;

    @Schema(description = "총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "주문 상품 목록")
    private List<OrderItemResponse> items;  // OrderItemResponse는 별도 파일로 분리
}