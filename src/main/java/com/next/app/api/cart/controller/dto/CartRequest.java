package com.next.app.api.cart.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "CartRequest")
public class CartRequest {

    @NotNull
    @Schema(description = "상품 ID", example = "101")
    private Long productId;

    @Min(1)
    @Schema(description = "수량", example = "2")
    private int quantity;
}
