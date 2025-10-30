package com.project.techstore.controllers;

import com.project.techstore.dtos.UpdateCartItemRequest;
import com.project.techstore.dtos.cart.AddToCartRequest;
import com.project.techstore.dtos.cart.CartDTO;
import com.project.techstore.models.User;
import com.project.techstore.responses.ApiResponse;
import com.project.techstore.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;

    /**
     * GET /api/v1/cart - Lấy giỏ hàng của user hiện tại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@AuthenticationPrincipal User currentUser) throws Exception {
        CartDTO cart = cartService.getCartByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    /**
     * POST /api/v1/cart/items - Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody AddToCartRequest request,
        BindingResult result) throws Exception {
    if (result.hasErrors()) {
        List<String> errorMessages = result.getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .toList();
        return ResponseEntity.badRequest()
        .body(ApiResponse.error(String.join(", ", errorMessages)));
    }
    CartDTO cart = cartService.addToCart(user.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.ok(cart));
    }

    /**
     * PUT /api/v1/cart/items/{cartItemId} - Cập nhật số lượng sản phẩm trong giỏ
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateCartItem(
            @PathVariable String cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal User user,
            BindingResult result) throws Exception{
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(String.join(", ", errorMessages)));
        }
        CartDTO cart = cartService.updateCartItem(user.getId(), cartItemId, request);
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    /**
     * DELETE /api/v1/cart/items/{cartItemId} - Xóa sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeFromCart(@PathVariable String cartItemId,
        @AuthenticationPrincipal User user) throws Exception {
        CartDTO cart = cartService.removeFromCart(user.getId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    /**
     * DELETE /api/v1/cart - Xóa toàn bộ giỏ hàng
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal User user) throws Exception {
        cartService.clearCart(user.getId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
