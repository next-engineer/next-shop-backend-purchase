package com.next.app.api.category.service;

import com.next.app.api.category.controller.dto.CategoryDto;
import com.next.app.api.category.entity.Category;
import com.next.app.api.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 카테고리 서비스
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    /**
     * 전체 카테고리 리스트 반환
     */
    public List<CategoryDto> list() {
        List<Category> categories = repository.findAll();
        return categories.stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .toList();
    }
}
