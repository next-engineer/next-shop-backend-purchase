package com.next.app.api.payment.controller;

import com.next.app.api.payment.controller.dto.PaymentRequestDto;
import com.next.app.api.payment.controller.dto.PaymentResponseDto;
import com.next.app.api.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment", description = "결제 API")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "결제 처리")
    public ResponseEntity<PaymentResponseDto> pay(@RequestBody PaymentRequestDto req) {
        return ResponseEntity.ok(paymentService.pay(req));
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "결제 취소")
    public ResponseEntity<PaymentResponseDto> cancel(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.cancelPayment(paymentId));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 단건 조회")
    public ResponseEntity<PaymentResponseDto> get(@PathVariable Long paymentId) {
        return paymentService.getPayment(paymentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
