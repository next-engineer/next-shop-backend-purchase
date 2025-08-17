package com.next.app.api.payment.entity;

import com.next.app.api.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 결제 정보 엔티티
 */
@Getter
@Setter
@Entity
@Table(name = "payments", catalog = "purchase")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 결제와 연결된 주문
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 결제 수단 (CARD, BANK 등)
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    // 카드 번호 (마스킹 처리 후 저장)
    @Column(name = "card_number", length = 50)
    private String cardNumber;

    // 은행 계좌 번호 (마스킹 처리 후 저장)
    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    // 결제 완료 시각
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // 결제 취소 시각
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
