package com.next.app.api.order.service;

import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderItem;
import static com.next.app.api.order.entity.OrderStatus.*;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.repository.OrderRepository;
import com.next.app.api.order.repository.OrderItemRepository;
import com.next.app.api.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.next.app.api.order.entity.OrderStatus.PENDING;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 전이 규칙
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            PENDING,   Set.of(PAID, CANCELLED),
            PAID,      Set.of(SHIPPED, CANCELLED, REFUNDED),
            SHIPPED,   Set.of(REFUNDED),
            CANCELLED, Set.of(),
            REFUNDED,  Set.of()
    );

    private void validateTransition(OrderStatus from, OrderStatus to) {
        if (!ALLOWED.getOrDefault(from, Set.of()).contains(to)) {
            throw new IllegalStateException("Invalid status transition: " + from + " -> " + to);
        }
    }

    //주문 생성
    public Order createOrder(User user, List<OrderItem> orderItems, String deliveryAddress) {
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setStatus(PENDING);
        order.setDeliveryAddress(deliveryAddress);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    //전체 주문 조회
    public List<Order> getAllOrdersWithItems() {
        return orderRepository.findAllWithItems();
    }

    public Optional<Order> getOrderByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    // 주문 상태 변경 (관리자/예외용)
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus next) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        validateTransition(order.getStatus(), next);
        order.setStatus(next);
        return orderRepository.save(order);
    }

    //특정 주문 조회
    public Order getOrder(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // 결제 시 상태 변경 (PaymentService에서 호출)
    @Transactional
    public void setOrderPaid(Long orderId) {
        updateOrderStatus(orderId, PAID);
    }

    @Transactional
    public void setOrderCancelled(Long orderId) {
        updateOrderStatus(orderId, CANCELLED);
    }

    //주문 삭제
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderItemRepository.deleteByOrderId(id);  // 자식 데이터 먼저 삭제
        orderRepository.deleteById(id);           // 부모 데이터 삭제
    }

}
