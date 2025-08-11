package com.next.app.api.cart.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@Schema(name = "CartItemResponse")
public class CartItemResponse {

    @Schema(description = "상품 ID", example = "101")
    private final Long productId;

    @Schema(description = "상품명", example = "무선 이어폰 Pro")
    private final String productName;

    @Schema(description = "수량", example = "2")
    private final int quantity;

    @Schema(description = "단가", example = "199000")
    private final BigDecimal price;

    @Schema(description = "합계(단가*수량)", example = "398000")
    private final BigDecimal lineTotal;
}
