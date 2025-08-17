package com.next.app.api.cart.controller;

import com.next.app.api.cart.controller.dto.CartRequest;
import com.next.app.api.cart.controller.dto.CartResponse;
import com.next.app.api.cart.service.CartService;
import com.next.app.security.CustomUserPrincipal;
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
@RequestMapping("/api/carts") // 프론트에 맞춰 복수형으로 통일
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 조회")
    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getId()));
    }

    @Operation(summary = "장바구니에 상품 추가 (기존: 바디로)")
    @PostMapping
    public ResponseEntity<CartResponse> addItem(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.getId(), request));
    }

    // === 프론트 호환용 alias 1: /api/carts/items (바디 동일) ===
    @Operation(summary = "장바구니에 상품 추가 (프론트 호환: /items)")
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemAliasBody(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                         @Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.getId(), request));
    }

    // === 프론트 호환용 alias 2: /api/carts/{productId}/items?quantity=1 (바디 없이) ===
    @Operation(summary = "장바구니에 상품 추가 (프론트 호환: /{productId}/items)")
    @PostMapping("/{productId}/items")
    public ResponseEntity<CartResponse> addItemAliasPath(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                         @PathVariable Long productId,
                                                         @RequestParam(name = "quantity", defaultValue = "1") int quantity) {
        CartRequest req = new CartRequest();
        req.setProductId(productId);
        req.setQuantity(quantity);
        return ResponseEntity.ok(cartService.addItem(principal.getId(), req));
    }

    @Operation(summary = "장바구니 수량 변경")
    @PutMapping("/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                       @PathVariable Long productId,
                                                       @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(principal.getId(), productId, quantity));
    }

    @Operation(summary = "장바구니 비우기")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomUserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
