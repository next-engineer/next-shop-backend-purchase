package com.next.app.api.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (User 엔티티 매핑 가능)
    @Column(nullable = false)
    private Long userId;

    // 장바구니 총 금액
    @Column(nullable = false)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    // 생성일, 수정일
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 장바구니 아이템 목록
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

    // 총 금액 계산 편의 메서드
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 아이템 추가 편의 메서드
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        calculateTotalPrice();
    }

    // 아이템 제거 편의 메서드
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        calculateTotalPrice();
    }
}
