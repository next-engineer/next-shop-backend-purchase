package com.next.app.api.product.entity;

import com.next.app.api.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 상품 Entity
 * - 상품명, 단가, 사진, 설명, 카테고리 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products", catalog = "purchase") // AWS RDS 환경에서 catalog(데이터베이스) 지정 필수
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품명
    @Column(nullable = false, length = 255)
    private String name;

    // 단가(필수)
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    // 상품 이미지 URL
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // 상품 설명(옵션)
    private String description;

    // 카테고리 (다대일 연관관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
