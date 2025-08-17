package com.next.app.api.product.repository;

import com.next.app.api.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 상품 Repository
 * - 카테고리/상품명 기반 필터링 지원
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 카테고리 ID로 상품 페이징 조회
    Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);

    // 상품명(부분 일치, 대소문자 무시)로 페이징 조회
    Page<Product> findByNameContainingIgnoreCase(String q, Pageable pageable);

    // 카테고리+상품명 필터 페이징 조회
    Page<Product> findByCategory_IdAndNameContainingIgnoreCase(Long categoryId, String q, Pageable pageable);
}
