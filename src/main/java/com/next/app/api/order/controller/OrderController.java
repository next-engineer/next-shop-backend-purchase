package com.next.app.api.order.controller;

import com.next.app.api.order.controller.dto.OrderItemResponse;
import com.next.app.api.order.controller.dto.OrderRequest;
import com.next.app.api.order.controller.dto.OrderResponse;
import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderItem;
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

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @Operation(summary = "주문 단건 조회")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id,
                                                      @AuthenticationPrincipal CustomUserPrincipal principal) {
        if (!orderService.isOrderOwner(id, principal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return orderService.getOrder(id)
                .map(o -> ResponseEntity.ok(toResponse(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "내 주문 이력 조회")
    public ResponseEntity<List<OrderResponse>> listByUser(@AuthenticationPrincipal CustomUserPrincipal principal) {
        List<Order> orders = orderService.listByUser(principal.getId());
        return ResponseEntity.ok(orders.stream().map(this::toResponse).toList());
    }

    @PostMapping
    @Operation(summary = "주문 생성")
    public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                @RequestBody OrderRequest request) {
        Order created = orderService.createOrder(request, principal.getId());
        return ResponseEntity.ok(toResponse(created));
    }

    @PostMapping("/checkout")
    @Operation(summary = "장바구니 주문 생성 후 비우기")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Order created = orderService.createOrderFromCart(principal.getId());
        return ResponseEntity.ok(toResponse(created));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "주문 상태 변경")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "주문 삭제")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getOrderItems() == null ? List.of() :
                o.getOrderItems().stream().map(this::toItemResponse).collect(Collectors.toList());
        return OrderResponse.builder()
                .orderId(o.getId())
                .userId(o.getUser() != null ? o.getUser().getId() : null)
                .deliveryAddress(o.getDeliveryAddress())
                .status(o.getStatus() != null ? o.getStatus().name() : null)
                .totalPrice(o.getTotalPrice())
                .createdAt(o.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem oi) {
        BigDecimal lineTotal = (oi.getPrice() != null ? oi.getPrice() : BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(oi.getQuantity()));
        return OrderItemResponse.builder()
                .productId(oi.getProduct() != null ? oi.getProduct().getId() : null)
                .productName(oi.getProduct() != null ? oi.getProduct().getName() : null)
                .quantity(oi.getQuantity())
                .price(oi.getPrice())
                .lineTotal(lineTotal)
                .build();
    }
}
