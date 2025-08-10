package com.next.app.api.payment.dto;

import java.time.LocalDateTime;

public class PaymentRequestDto {
    private Long orderId;
    private String paymentMethod; // CARD or BANK
    private String paymentInfo;   // 카드번호 또는 계좌번호 (원문 - 서비스가 마스킹하여 저장)

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(String paymentInfo) { this.paymentInfo = paymentInfo; }
}
