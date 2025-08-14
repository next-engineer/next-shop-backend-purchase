package com.next.app.api.cart.controller;

import com.next.app.api.cart.controller.dto.CartRequest;
import com.next.app.api.cart.controller.dto.CartResponse;
import com.next.app.api.cart.service.CartService;
import com.next.app.api.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart API", description = "장바구니 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 조회")
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(cartService.getCart(user.getId()));
    }

    @Operation(summary = "장바구니에 상품 추가")
    @PostMapping
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal User user,
                                                @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addItem(user.getId(), request));
    }

    @Operation(summary = "장바구니 수량 변경")
    @PutMapping("/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(@AuthenticationPrincipal User user,
                                                       @PathVariable Long productId,
                                                       @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(user.getId(), productId, quantity));
    }

    @Operation(summary = "장바구니 비우기")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
        return ResponseEntity.noContent().build();
    }
}

