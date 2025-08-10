package com.next.app.api.payment.entity;

import com.next.app.api.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String paymentMethod; // CARD, BANK

    @Column
    private String cardNumber;

    @Column
    private String bankAccount;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Column
    private LocalDateTime cancelledAt;
}
