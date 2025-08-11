package com.next.app.api.cart.controller;

import com.next.app.api.cart.controller.dto.CartRequest;
import com.next.app.api.cart.controller.dto.CartResponse;
import com.next.app.api.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart API", description = "장바구니 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 조회", description = "유저 ID로 장바구니 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @Operation(summary = "장바구니에 상품 추가", description = "유저 장바구니에 상품과 수량을 추가합니다.")
    @PostMapping
    public ResponseEntity<CartResponse> addItem(
            @RequestParam Long userId,
            @Valid @RequestBody CartRequest request
    ) {
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @Operation(summary = "장바구니 수량 변경", description = "유저 장바구니에서 특정 상품을 제거합니다.")
    @PutMapping("/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @RequestParam Long userId,
            @PathVariable Long productId,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, productId, quantity));
    }

    @Operation(summary = "장바구니 비우기", description = "유저 장바구니 내 모든 상품을 제거합니다.")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
