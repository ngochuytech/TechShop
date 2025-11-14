package com.project.techstore.services;

import com.project.techstore.dtos.cart.AddToCartRequest;
import com.project.techstore.dtos.cart.CartDTO;
import com.project.techstore.dtos.cart.CartItemDTO;
import com.project.techstore.dtos.cart.UpdateCartItemRequest;
import com.project.techstore.exceptions.DataNotFoundException;
import com.project.techstore.models.*;
import com.project.techstore.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public CartDTO getCartByUserId(String userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(user));
        
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(String userId, AddToCartRequest request) throws Exception {
        // Validate request
        if (!request.isValid()) {
            throw new IllegalArgumentException("Yêu cầu 1 trong 2 trường productId hoặc productVariantId phải có");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        
        Product product = null;
        ProductVariant productVariant = null;
        Long price;
        Integer availableStock;
        
        // Xác định đang thêm Product hay ProductVariant
        if (request.getProductVariantId() != null && !request.getProductVariantId().isEmpty()) {
            // Thêm ProductVariant
            productVariant = productVariantRepository.findById(request.getProductVariantId())
                    .orElseThrow(() -> new DataNotFoundException("Product variant not found"));
            price = productVariant.getPrice();
            availableStock = productVariant.getStock();
        } else {
            // Thêm Product
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));
            price = product.getPrice();
            availableStock = product.getStock();
        }
        
        // Kiểm tra số lượng tồn kho
        if (availableStock < request.getQuantity()) {
            throw new IllegalArgumentException("Sản phẩm tồn kho không khả dung. Số lượng hiện có: " + availableStock);
        }
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(user));
        
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Optional<CartItem> existingItem;
        if (productVariant != null) {
            existingItem = cartItemRepository
                    .findByCartIdAndProductVariantId(cart.getId(), request.getProductVariantId());
        } else {
            existingItem = cartItemRepository
                    .findByCartIdAndProductId(cart.getId(), request.getProductId());
        }
        
        if (existingItem.isPresent()) {
            // Nếu đã có, tăng số lượng
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            if (availableStock < newQuantity) {
                throw new IllegalArgumentException("Sản phẩm tồn kho không khả dung. Số lượng hiện có: " + availableStock);
            }
            
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Nếu chưa có, tạo mới
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .productVariant(productVariant)
                    .quantity(request.getQuantity())
                    .price(price)
                    .build();
            
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }
        
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO updateCartItem(String userId, String cartItemId, UpdateCartItemRequest request) throws Exception {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));
        
        // Kiểm tra cart item có thuộc cart của user không
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }
        
        // Kiểm tra số lượng tồn kho
        Integer availableStock = cartItem.getAvailableStock();
        if (availableStock < request.getQuantity()) {
            throw new IllegalArgumentException("Sản phẩm tồn kho không khả dung. Số lượng hiện có: " + availableStock);
        }
        
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        return convertToDTO(cart);
    }

    @Transactional
    public CartDTO removeFromCart(String userId, String cartItemId) throws Exception {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
        
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new DataNotFoundException("Cart item not found"));
        
        // Kiểm tra cart item có thuộc cart của user không
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }
        
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        
        return convertToDTO(cart);
    }

    @Transactional
    public void clearCart(String userId) throws Exception {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("Cart not found"));
        
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart createCartForUser(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        
        dto.setItems(itemDTOs);
        dto.setTotalQuantity(cart.getTotalQuantity());
        dto.setTotalPrice(cart.getTotalPrice());
        
        return dto;
    }

    private CartItemDTO convertItemToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setVariant(item.isProductVariant());
        
        if (item.isProductVariant()) {
            // Nếu là ProductVariant
            dto.setProductVariantId(item.getProductVariant().getId());
            dto.setProductId(item.getProductVariant().getProduct().getId());
            dto.setProductName(item.getProductVariant().getProduct().getName());
            dto.setColor(item.getProductVariant().getColor());
            dto.setImage(item.getProductVariant().getImage());
            dto.setAvailableStock(item.getProductVariant().getStock());
        } else {
            // Nếu là Product
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setImage(item.getProductImage());
            dto.setAvailableStock(item.getProduct().getStock());
            dto.setColor(null);
        }
        
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSubtotal(item.getSubtotal());
        
        return dto;
    }
}
