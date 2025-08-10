package com.next.app.api.order.service;

import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderItem;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.repository.OrderItemRepository;
import com.next.app.api.order.repository.OrderRepository;
import com.next.app.api.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.next.app.api.order.entity.OrderStatus.*;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(User user, List<OrderItem> orderItems, String deliveryAddress) {
        BigDecimal totalPrice = orderItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(totalPrice);
        order.setStatus(PENDING);
        order.setDeliveryAddress(deliveryAddress);
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        for (OrderItem item : orderItems) {
            item.setOrder(saved);
            orderItemRepository.save(item);
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderByIdWithItems(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrdersWithItems() {
        return orderRepository.findAllWithItems();
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus to) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        validateTransition(order.getStatus(), to);
        order.setStatus(to);
        return orderRepository.save(order);
    }

    private void validateTransition(OrderStatus from, OrderStatus to) {
        if (from == PENDING && (to == PAID || to == CANCELLED)) return;
        if (from == PAID && (to == SHIPPED || to == REFUNDED || to == CANCELLED)) return;
        if (from == SHIPPED && to == REFUNDED) return;
        if (from == CANCELLED || from == REFUNDED) throw new IllegalStateException("Terminal state");
        throw new IllegalStateException("Invalid status transition: " + from + " -> " + to);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, CANCELLED);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) throw new RuntimeException("Order not found: " + id);
        orderItemRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
    }
}
