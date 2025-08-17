package com.next.app.api.order.controller;

import com.next.app.api.order.controller.dto.OrderRequest;
import com.next.app.api.order.controller.dto.OrderResponse;
import com.next.app.api.order.controller.dto.OrderItemResponse;
import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.service.OrderService;
import com.next.app.security.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                     @RequestBody OrderRequest request) {
        // 사용자가 입력한 배송지 그대로 사용 (null일 경우 빈 문자열로 처리 가능)
        String deliveryAddress = request.getDelivery_address() != null ? request.getDelivery_address() : "";
        Order order = orderService.createOrder(request, principal.getId(), deliveryAddress);
        return ResponseEntity.ok(toResponse(order));
    }

    // 주문 단건조회, 주문 목록 등 필요한 메서드도 동일하게 추가 가능

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .delivery_address(order.getDelivery_address())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(order.getOrderItems() == null ? List.of() :
                        order.getOrderItems().stream()
                                .map(oi -> new OrderItemResponse(
                                        oi.getProduct() != null ? oi.getProduct().getId() : null,
                                        oi.getProduct() != null ? oi.getProduct().getName() : null,
                                        oi.getQuantity(),
                                        oi.getPrice(),
                                        oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()))
                                ))
                                .collect(Collectors.toList()))
                .build();
    }
}
