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
import org.springframework.http.HttpStatus;
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

    @GetMapping("/{id}")
    @Operation(summary = "주문 단건 조회")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id,
                                                  @AuthenticationPrincipal CustomUserPrincipal principal) {
        if (!orderService.isOrderOwner(id, principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Order order = orderService.getOrderOrThrow(id);
        return ResponseEntity.ok(toResponse(order));
    }

    @GetMapping
    @Operation(summary = "내 주문 목록")
    public ResponseEntity<List<OrderResponse>> listByUser(@AuthenticationPrincipal CustomUserPrincipal principal) {
        List<Order> orders = orderService.listByUser(principal.getId());
        List<OrderResponse> response = orders.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "주문 생성")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                     @RequestBody OrderRequest request) {
        // 사용자 입력 배송지를 우선 사용, 빈 값일 경우 서비스에서 기본 배송지 처리 가능
        String deliveryAddress = (request.getDelivery_address() != null && !request.getDelivery_address().isBlank())
                ? request.getDelivery_address()
                : "";  // 빈 문자열 또는 필요한 기본 처리 로직 적용

        Order order = orderService.createOrder(request, principal.getId(), deliveryAddress);
        return ResponseEntity.ok(toResponse(order));
    }

    @PostMapping("/checkout")
    @Operation(summary = "장바구니 주문 생성")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                  @RequestBody(required = false) OrderRequest request) {
        String deliveryAddress = (request != null && request.getDelivery_address() != null && !request.getDelivery_address().isBlank())
                ? request.getDelivery_address()
                : ""; // 필요하면 서비스에서 기본배송지 설정

        Order order = orderService.createOrderFromCart(principal.getId(), deliveryAddress);
        return ResponseEntity.ok(toResponse(order));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "주문 상태 변경")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "주문 삭제")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

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
