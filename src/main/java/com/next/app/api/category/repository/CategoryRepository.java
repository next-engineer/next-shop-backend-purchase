package com.next.app.api.category.repository;

import com.next.app.api.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 카테고리 저장소
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
