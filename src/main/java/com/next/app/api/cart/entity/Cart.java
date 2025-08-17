package com.next.app.api.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 Entity
 * - 회원 한 명이 하나의 장바구니 보유, 여러 상품 담을 수 있음
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts", catalog = "purchase")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (User 엔티티 매핑 없이 값만)
    @Column(nullable = false)
    private Long userId;

    // 장바구니 총 금액
    @Column(nullable = false)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    // 생성·수정 시각
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 장바구니에 담긴 아이템 목록(양방향)
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 총 금액 계산
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 아이템 추가
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        calculateTotalPrice();
    }

    // 아이템 제거
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        calculateTotalPrice();
    }
}
