package com.next.app.api.order.controller;

import com.next.app.api.order.controller.dto.OrderItemResponse;
import com.next.app.api.order.controller.dto.OrderRequest;
import com.next.app.api.order.controller.dto.OrderResponse;
import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderItem;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.service.OrderService;
import com.next.app.api.product.entity.Product;
import com.next.app.api.product.repository.ProductRepository;
import com.next.app.api.user.entity.User;
import com.next.app.api.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "주문 API")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping
    @Operation(summary = "주문 목록 조회")
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderService.getAllOrdersWithItems();
        return orders.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "주문 단건 조회")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderService.getOrderByIdWithItems(id)
                .map(o -> ResponseEntity.ok(toResponse(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "주문 생성")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        List<OrderItem> items = request.getItems().stream().map(i -> {
            Product product = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + i.getProductId()));
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(i.getQuantity());
            item.setPrice(BigDecimal.valueOf(product.getPrice()));
            return item;
        }).toList();

        Order order = orderService.createOrder(user, items, request.getDeliveryAddress());
        return ResponseEntity.ok(toResponse(order));
    }

    @PutMapping("/{id}")
    @Operation(summary = "주문 상태 변경", description = "PENDING, PAID, SHIPPED, CANCELLED, REFUNDED")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "주문 삭제")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> itemResponses = o.getOrderItems().stream()
                .map(this::toItemResponse).toList();

        return new OrderResponse(
                o.getId(),
                (o.getUser() != null ? o.getUser().getId() : null),
                o.getDeliveryAddress(),
                (o.getStatus() != null ? o.getStatus().name() : null),
                o.getTotalPrice(),
                o.getCreatedAt(),
                itemResponses
        );
    }

    private OrderItemResponse toItemResponse(OrderItem oi) {
        BigDecimal lineTotal = oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()));
        return new OrderItemResponse(
                oi.getProduct() != null ? oi.getProduct().getId() : null,
                oi.getProduct() != null ? oi.getProduct().getName() : null,
                oi.getQuantity(),
                oi.getPrice(),
                lineTotal
        );
    }
}
