package com.next.app.api.order.service;

import com.next.app.api.cart.entity.Cart;
import com.next.app.api.cart.entity.CartItem;
import com.next.app.api.cart.repository.CartRepository;
import com.next.app.api.order.controller.dto.OrderRequest;
import com.next.app.api.order.entity.Order;
import com.next.app.api.order.entity.OrderItem;
import com.next.app.api.order.entity.OrderStatus;
import com.next.app.api.order.repository.OrderRepository;
import com.next.app.api.product.entity.Product;
import com.next.app.api.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Order> listByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order createOrderFromCart(Long userId, String deliveryAddress) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setDelivery_address(deliveryAddress);
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
            orderItem.setPrice(cartItem.getPrice() != null ? cartItem.getPrice() : BigDecimal.ZERO);

            total = total.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            order.getOrderItems().add(orderItem);
        }

        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return savedOrder;
    }

    public Order createOrder(OrderRequest request, Long userId, String deliveryAddress) {
        if (request == null)
            throw new IllegalArgumentException("주문 요청이 누락되었습니다.");
        if (request.getItems() == null || request.getItems().isEmpty())
            throw new IllegalArgumentException("주문하려는 상품이 없습니다.");

        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setDelivery_address(deliveryAddress);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderRequest.Item item : request.getItems()) {
            if (item.getProductId() == null || item.getQuantity() <= 0)
                throw new IllegalArgumentException("주문 아이템 정보가 부적절합니다.");

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());

            BigDecimal price = item.getPrice() != null ? item.getPrice() : product.getPrice();
            orderItem.setPrice(price != null ? price : BigDecimal.ZERO);

            total = total.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            order.getOrderItems().add(orderItem);
        }

        order.setTotalPrice(total);

        return orderRepository.save(order);
    }

    public void updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public boolean isOrderOwner(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(o -> userId.equals(o.getUserId()))
                .orElse(false);
    }
}
