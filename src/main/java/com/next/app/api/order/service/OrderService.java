package com.next.app.api.order.service;

import com.next.app.api.cart.entity.Cart;
import com.next.app.api.cart.entity.CartItem;
import com.next.app.api.cart.repository.CartItemRepository;
import com.next.app.api.cart.repository.CartRepository;
import com.next.app.api.order.controller.dto.OrderRequest;
import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderItem;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.repository.OrderItemRepository;
import com.next.app.api.order.repository.OrderRepository;
import com.next.app.api.product.entity.Product;
import com.next.app.api.product.repository.ProductRepository;
import com.next.app.api.user.entity.User;
import com.next.app.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        if (request == null || request.getUserId() == null) throw new IllegalArgumentException("userId is required");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(user.getDelivery_address());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        if (request.getItems() != null) {
            for (OrderRequest.Item it : request.getItems()) {
                if (it.getProductId() == null) throw new IllegalArgumentException("productId is required");

                Product product = productRepository.findById(it.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + it.getProductId()));

                OrderItem oi = new OrderItem();
                oi.setOrder(order);
                oi.setProduct(product);
                oi.setQuantity(it.getQuantity());
                BigDecimal unitPrice = it.getPrice() != null ? it.getPrice() : product.getPrice();
                oi.setPrice(unitPrice);
                orderItems.add(oi);

                total = total.add(unitPrice.multiply(BigDecimal.valueOf(it.getQuantity())));
            }
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(total);

        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrderFromCart(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) throw new IllegalStateException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(user.getDelivery_address());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cart.getItems()) {
            Product product = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ci.getProductId()));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(ci.getQuantity());
            BigDecimal unitPrice = product.getPrice();
            oi.setPrice(unitPrice);
            orderItems.add(oi);

            total = total.add(unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Order> listByUser(Long userId) {
        return orderRepository.findByUserIdWithItems(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrder(Long orderId) {
        return orderRepository.findByIdWithItems(orderId);
    }

    @Transactional(readOnly = true)
    public Order getOrderOrThrow(Long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderOrThrow(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) throw new RuntimeException("Order not found: " + id);
        orderItemRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
    }
}
