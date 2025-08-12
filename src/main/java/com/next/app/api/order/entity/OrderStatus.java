package com.next.app.api.order.entity;

public enum OrderStatus {
    PENDING,    // 결제 대기
    PAID,       // 결제 완료
    SHIPPED,    // 배송 중
    CANCELLED,  // 주문 취소
    REFUNDED    // 환불 완료
}