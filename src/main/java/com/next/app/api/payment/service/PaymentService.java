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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public PaymentResponseDto pay(PaymentRequestDto req) {
        Order order = orderService.getOrderByIdWithItems(req.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + req.getOrderId()));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order status must be PENDING");
        }

        Payment p = new Payment();
        p.setOrder(order);
        p.setPaymentMethod(req.getPaymentMethod());

        if ("CARD".equalsIgnoreCase(req.getPaymentMethod())) {
            p.setCardNumber(maskCard(req.getPaymentInfo()));
            p.setBankAccount(null);
        } else if ("BANK".equalsIgnoreCase(req.getPaymentMethod())) {
            p.setBankAccount(maskBank(req.getPaymentInfo()));
            p.setCardNumber(null);
        } else {
            throw new IllegalArgumentException("Unsupported payment method");
        }

        p.setPaidAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(p);

        orderService.updateOrderStatus(order.getId(), OrderStatus.PAID);

        return toDto(saved);
    }

    @Transactional
    public PaymentResponseDto cancelPayment(Long paymentId) {
        Payment p = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (p.getCancelledAt() != null) {
            return toDto(p);
        }

        p.setCancelledAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(p);

        if (p.getOrder() != null) {
            orderService.updateOrderStatus(p.getOrder().getId(), OrderStatus.CANCELLED);
        }

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<PaymentResponseDto> getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).map(this::toDto);
    }

    private String maskCard(String raw) {
        if (raw == null || raw.length() < 4) return "****";
        return "****-****-****-" + raw.substring(raw.length() - 4);
    }

    private String maskBank(String raw) {
        if (raw == null || raw.length() < 4) return "****";
        return "****" + raw.substring(raw.length() - 4);
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
