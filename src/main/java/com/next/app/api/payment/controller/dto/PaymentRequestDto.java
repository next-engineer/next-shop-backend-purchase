package com.next.app.api.payment.controller.dto;

/**
 * 결제 요청 DTO
 */
public class PaymentRequestDto {

    private Long orderId;

    private String paymentMethod; // "CARD", "BANK" 등

    private String paymentInfo; // 원본 카드번호 또는 계좌번호

    // getters and setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(String paymentInfo) { this.paymentInfo = paymentInfo; }
}
