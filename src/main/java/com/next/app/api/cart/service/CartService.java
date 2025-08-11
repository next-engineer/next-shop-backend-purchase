package com.next.app.api.cart.service;

import com.next.app.api.cart.dto.CartItemDTO;
import com.next.app.api.cart.entity.Cart;
import com.next.app.api.cart.entity.CartItem;
import com.next.app.api.product.entity.Product;
import com.next.app.api.user.entity.User;
import com.next.app.api.cart.repository.CartItemRepository;
import com.next.app.api.cart.repository.CartRepository;
import com.next.app.api.product.repository.ProductRepository;
import com.next.app.api.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // 유저 ID로 장바구니 조회 (존재하지 않으면 null 반환)
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUser_Id(userId);
    }

    // 장바구니에 상품 추가 (DTO 반환)
    public CartItemDTO addProductToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        Cart cart = cartRepository.findByUser_Id(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));

        Optional<CartItem> cartItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        CartItem cartItem;
        if (cartItemOpt.isPresent()) {
            cartItem = cartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }
        CartItem saved = cartItemRepository.save(cartItem);

        return new CartItemDTO(
                saved.getId(),
                saved.getProduct().getId(),
                saved.getProduct().getName(),
                saved.getQuantity()
        );
    }

    // 유저 장바구니 항목 조회 (DTO 리스트 반환)
    public List<CartItemDTO> getCartItemsByUserId(Long userId) {
        Cart cart = cartRepository.findByUser_Id(userId);
        if (cart == null) {
            return Collections.emptyList();
        }
        List<CartItem> items = cartItemRepository.findByCart(cart);
        return items.stream()
                .map(item -> new CartItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity()))
                .collect(Collectors.toList());
    }

    // 장바구니에서 특정 상품 제거
    public void removeProductFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUser_Id(userId);
        if (cart == null) {
            throw new RuntimeException("장바구니가 없습니다: " + userId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + productId));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다."));

        cartItemRepository.delete(cartItem);
    }

    // 장바구니 비우기
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUser_Id(userId);
        if (cart == null) {
            throw new RuntimeException("장바구니가 없습니다: " + userId);
        }
        List<CartItem> items = cartItemRepository.findByCart(cart);
        cartItemRepository.deleteAll(items);
    }

    // 개별 장바구니 아이템 제거 (필요시)
    public void removeItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
