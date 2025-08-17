package com.next.app.api.product.controller.dto;

import java.math.BigDecimal;

/**
 * 상품에 대한 데이터 전송 객체(DTO)
 */
public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        String imageUrl,
        Long categoryId
) {}

