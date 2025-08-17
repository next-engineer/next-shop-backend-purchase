package com.next.app.api.cart.repository;

import com.next.app.api.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 장바구니 저장소
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Optional<Cart> findByIdAndUserId(Long id, Long userId);
}
