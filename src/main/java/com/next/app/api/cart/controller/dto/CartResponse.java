package com.next.app.api.cart.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import com.next.app.api.cart.controller.dto.CartItemResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(name = "CartResponse")
public class CartResponse {

    @Schema(description = "장바구니 ID", example = "12")
    private final Long cartId;

    @Schema(description = "사용자 ID", example = "7")
    private final Long userId;

    @Schema(description = "아이템 목록")
    private final List<CartItemResponse> items;

    @Schema(description = "총액", example = "398000")
    private final BigDecimal totalPrice;

    @Schema(description = "생성일시")
    private final LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private final LocalDateTime updatedAt;
}
