package com.next.app.api.payment.controller.dto;

import java.time.LocalDateTime;

/**
 * 결제 응답 DTO
 */
public class PaymentResponseDto {

    private Long id;

    private Long orderId;

    private String paymentMethod;

    private String cardNumber; // 마스킹 처리 예: ****-****-****-1234

    private String bankAccount; // 마스킹 처리 예: ****1234

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    // getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) {
        if (cardNumber != null && cardNumber.length() > 4) {
            this.cardNumber = "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
        } else {
            this.cardNumber = cardNumber;
        }
    }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) {
        if (bankAccount != null && bankAccount.length() > 4) {
            this.bankAccount = "****" + bankAccount.substring(bankAccount.length() - 4);
        } else {
            this.bankAccount = bankAccount;
        }
    }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
}
