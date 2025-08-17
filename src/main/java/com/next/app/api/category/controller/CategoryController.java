package com.next.app.api.category.controller;

import com.next.app.api.category.controller.dto.CategoryDto;
import com.next.app.api.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 카테고리 API 컨트롤러
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    /**
     * 전체 카테고리 조회
     */
    @GetMapping
    public List<CategoryDto> list() {
        return service.list();
    }
}
