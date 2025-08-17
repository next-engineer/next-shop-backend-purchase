package com.next.app.api.product.controller;

import com.next.app.api.product.controller.dto.ProductDto;
import com.next.app.api.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 API 컨트롤러
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // 상품 목록 조회 (조건: 카테고리/검색어/페이징 지원)
    @GetMapping
    public Page<ProductDto> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        return service.list(q, categoryId, pageable);
    }

    // 단건 상품 상세
    @GetMapping("/{id}")
    public ProductDto get(@PathVariable Long id) {
        return service.get(id);
    }
}
