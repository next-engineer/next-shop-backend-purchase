package com.next.app.api.cart.service;

import com.next.app.api.cart.controller.dto.CartItemResponse;
import com.next.app.api.cart.controller.dto.CartRequest;
import com.next.app.api.cart.controller.dto.CartResponse;
import com.next.app.api.cart.entity.Cart;
import com.next.app.api.cart.entity.CartItem;
import com.next.app.api.cart.repository.CartItemRepository;
import com.next.app.api.cart.repository.CartRepository;
import com.next.app.api.product.entity.Product;
import com.next.app.api.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));

        return toCartResponse(cart);
    }

    public CartResponse addItem(Long userId, CartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProductId(product.getId());
                    newItem.setQuantity(0);
                    newItem.setPrice(product.getPrice());
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem.setPrice(product.getPrice()); // 가격 보정
        cartItemRepository.save(cartItem);

        cart.addItem(cartItem);
        cartRepository.save(cart);

        return toCartResponse(cart);
    }

    public CartResponse updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니에 해당 상품이 없습니다."));

        if (quantity <= 0) {
            cart.removeItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return toCartResponse(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cart.calculateTotalPrice();
        cartRepository.save(cart);
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(productRepository.findById(item.getProductId())
                                .map(Product::getName).orElse("삭제된 상품"))
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalPrice(cart.getTotalPrice())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
