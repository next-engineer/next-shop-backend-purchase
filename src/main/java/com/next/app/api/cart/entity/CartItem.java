package com.next.app.api.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 장바구니에 담긴 개별 상품 정보
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cart_items", catalog = "purchase")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 장바구니에 속해있는지 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // 상품 ID (Product 엔티티 매핑 없이 값만)
    @Column(nullable = false)
    private Long productId;

    // 수량
    @Column(nullable = false)
    private int quantity;

    // 단가
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    // 생성·수정 시각
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.price == null) this.price = BigDecimal.ZERO;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 이 아이템의 총 금액(단가 * 수량)
    public BigDecimal getLineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
