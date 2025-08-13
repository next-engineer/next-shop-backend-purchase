package com.next.app.api.payment.controller;

import com.next.app.api.payment.controller.dto.PaymentRequestDto;
import com.next.app.api.payment.controller.dto.PaymentResponseDto;
import com.next.app.api.payment.service.PaymentService;
import com.next.app.api.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payment API", description = "결제 API")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "결제 생성/승인")
    public ResponseEntity<PaymentResponseDto> pay(@AuthenticationPrincipal User user,
                                                  @RequestBody PaymentRequestDto req) {
        paymentService.verifyOrderOwnership(req.getOrderId(), user.getId());
        return ResponseEntity.ok(paymentService.pay(req));
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "결제 취소/환불")
    public ResponseEntity<PaymentResponseDto> cancel(@AuthenticationPrincipal User user,
                                                     @PathVariable Long paymentId) {
        paymentService.verifyPaymentOwnership(paymentId, user.getId());
        return ResponseEntity.ok(paymentService.cancel(paymentId));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 단건 조회")
    public ResponseEntity<PaymentResponseDto> get(@PathVariable Long paymentId) {
        return paymentService.getPayment(paymentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "주문 기준 결제 이력 조회")
    public ResponseEntity<List<PaymentResponseDto>> listByOrder(@AuthenticationPrincipal User user,
                                                                @RequestParam Long orderId) {
        paymentService.verifyOrderOwnership(orderId, user.getId());
        return ResponseEntity.ok(paymentService.listByOrderId(orderId));
    }
}

