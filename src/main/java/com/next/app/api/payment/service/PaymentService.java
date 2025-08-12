package com.next.app.api.payment.service;

import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.service.OrderService;
import com.next.app.api.payment.controller.dto.PaymentRequestDto;
import com.next.app.api.payment.controller.dto.PaymentResponseDto;
import com.next.app.api.payment.entity.Payment;
import com.next.app.api.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public PaymentResponseDto pay(PaymentRequestDto req) {
        if (req == null || req.getOrderId() == null) throw new IllegalArgumentException("orderId is required");
        if (req.getPaymentMethod() == null) throw new IllegalArgumentException("paymentMethod is required");

        Order order = orderService.getOrderOrThrow(req.getOrderId());
        if (order.getStatus() == OrderStatus.PAID) throw new IllegalStateException("Order is already PAID");

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(req.getPaymentMethod().toUpperCase());
        switch (payment.getPaymentMethod()) {
            case "CARD" -> payment.setCardNumber(maskCard(req.getPaymentInfo()));
            case "BANK" -> payment.setBankAccount(maskAccount(req.getPaymentInfo()));
            default -> throw new IllegalArgumentException("Unsupported payment method: " + req.getPaymentMethod());
        }
        payment.setPaidAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        orderService.updateOrderStatus(order.getId(), OrderStatus.PAID);
        return toDto(saved);
    }

    @Transactional
    public PaymentResponseDto cancel(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (payment.getCancelledAt() != null) return toDto(payment);

        payment.setCancelledAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        Order order = payment.getOrder();
        if (order != null) orderService.updateOrderStatus(order.getId(), OrderStatus.CANCELLED);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<PaymentResponseDto> getPayment(Long id) {
        return paymentRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> listByOrderId(Long orderId) {
        return paymentRepository.findByOrder_Id(orderId).stream().map(this::toDto).toList();
    }

    private String maskCard(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() < 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "****-****-****-" + last4;
    }

    private String maskAccount(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() <= 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "****" + last4;
    }

    private PaymentResponseDto toDto(Payment p) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(p.getId());
        dto.setOrderId(p.getOrder() != null ? p.getOrder().getId() : null);
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setCardNumber(p.getCardNumber());
        dto.setBankAccount(p.getBankAccount());
        dto.setPaidAt(p.getPaidAt());
        dto.setCancelledAt(p.getCancelledAt());
        return dto;
    }
}
