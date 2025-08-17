package com.next.app.api.payment.service;

import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.service.OrderService;
import com.next.app.api.payment.controller.dto.PaymentRequestDto;
import com.next.app.api.payment.controller.dto.PaymentResponseDto;
import com.next.app.api.payment.entity.Payment;
import com.next.app.api.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 결제 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    /**
     * 결제 승인 및 저장 처리
     */
    @Transactional
    public PaymentResponseDto pay(PaymentRequestDto req) {
        if (req == null || req.getOrderId() == null) {
            throw new IllegalArgumentException("orderId is required");
        }
        if (req.getPaymentMethod() == null) {
            throw new IllegalArgumentException("paymentMethod is required");
        }

        Order order = orderService.getOrderOrThrow(req.getOrderId());
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already PAID");
        }

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

    /**
     * 결제 취소 처리
     */
    @Transactional
    public PaymentResponseDto cancel(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (payment.getCancelledAt() != null) {
            return toDto(payment);
        }

        payment.setCancelledAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        Order order = payment.getOrder();
        if (order != null) {
            orderService.updateOrderStatus(order.getId(), OrderStatus.CANCELLED);
        }

        return toDto(saved);
    }

    /**
     * 결제 단건 조회
     */
    @Transactional(readOnly = true)
    public Optional<PaymentResponseDto> getPayment(Long id) {
        return paymentRepository.findById(id).map(this::toDto);
    }

    /**
     * 주문별 결제 이력 조회
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> listByOrderId(Long orderId) {
        return paymentRepository.findByOrder_Id(orderId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * 주문 소유자 검증
     */
    public void verifyOrderOwnership(Long orderId, Long userId) {
        if (!orderService.isOrderOwner(orderId, userId)) {
            throw new AccessDeniedException("해당 주문에 접근할 수 없습니다.");
        }
    }

    /**
     * 결제 소유자 검증
     */
    public void verifyPaymentOwnership(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));
        if (!orderService.isOrderOwner(payment.getOrder().getId(), userId)) {
            throw new AccessDeniedException("해당 결제에 접근할 수 없습니다.");
        }
    }

    // 카드번호 마스킹
    private String maskCard(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() < 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "****-****-****-" + last4;
    }

    // 계좌번호 마스킹
    private String maskAccount(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String digits = raw.replaceAll("\\D", "");
        if (digits.length() <= 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "****" + last4;
    }

    // 엔티티 -> DTO 변환
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
