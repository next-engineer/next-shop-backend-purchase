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
@Tag(name = "Order Controller", description = "주문 관리 API")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping
    @Operation(summary = "모든 주문 조회", description = "주문자/배송지/아이템/총액/상태 포함")
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderService.getAllOrdersWithItems();
        return orders.stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "주문 조회", description = "주문자/배송지/아이템/총액/상태 포함")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderService.getOrderByIdWithItems(id)
                .map(o -> ResponseEntity.ok(toResponse(o)))
                .orElse(ResponseEntity.notFound().build());
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> itemResponses = o.getOrderItems().stream()
                .map(this::toItemResponse)
                .toList();

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
        BigDecimal price = oi.getPrice(); // order_items 저장된 단가
        BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(oi.getQuantity()));

        return new OrderItemResponse(
                (oi.getProduct() != null ? oi.getProduct().getId() : null),
                (oi.getProduct() != null ? oi.getProduct().getName() : null),
                oi.getQuantity(),
                price,
                lineTotal
        );
    }

    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문 생성.")
    public Order createOrder(@RequestBody OrderRequest request) {
        //기존 저장된 user_id 가져옴
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderItem> items = request.getItems().stream().map(i -> {

            //db에 저장된 product 정보를 사용
            Product product = productRepository.findById(i.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(i.getQuantity());
            item.setPrice(BigDecimal.valueOf(product.getPrice()));
            return item;
        }).toList();

        return orderService.createOrder(
                user,
                items,
                request.getDeliveryAddress()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "주문 수정", description = "주문 상태 수정. (PENDING, PAID, SHIPPED, CANCELLED, REFUNDED)")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestParam OrderStatus status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "주문 삭제", description = "주문 삭제.")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
