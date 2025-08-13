package com.next.app.api.order.service;

import com.next.app.api.cart.entity.Cart;
import com.next.app.api.cart.entity.CartItem;
import com.next.app.api.cart.repository.CartRepository;
import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderItem;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.repository.OrderRepository;
import com.next.app.api.product.entity.Product;
import com.next.app.api.product.repository.ProductRepository;
import com.next.app.api.user.entity.User;
import com.next.app.api.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Optional<Order> getOrder(Long id) {
        return orderRepository.findByIdWithItems(id);
    }

    @Transactional(readOnly = true)
    public Order getOrderOrThrow(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Order> listByUser(Long userId) {
        return orderRepository.findByUserIdWithItems(userId);
    }

    public Order createOrderFromCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(user.getDelivery_address()); // snake_case getter
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);

            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal price = cartItem.getPrice() != null ? cartItem.getPrice() : BigDecimal.ZERO;
            orderItem.setPrice(price);

            total = total.add(price.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            order.getOrderItems().add(orderItem);
        }

        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return savedOrder;
    }

    public Order createOrder(Object orderRequest, User user) {
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isOrderOwner(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser() != null && order.getUser().getId().equals(userId))
                .orElse(false);
    }
}
