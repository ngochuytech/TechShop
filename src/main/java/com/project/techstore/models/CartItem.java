package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Long price; // Giá tại thời điểm thêm vào giỏ

    /**
     * Tính subtotal cho item này
     */
    public Long getSubtotal() {
        return price * quantity;
    }

    /**
     * Kiểm tra xem item này là Product hay ProductVariant
     */
    public boolean isProductVariant() {
        return productVariant != null;
    }

    /**
     * Lấy tên sản phẩm
     */
    public String getProductName() {
        if (productVariant != null) {
            return productVariant.getProduct().getName();
        }
        return product != null ? product.getName() : null;
    }

    /**
     * Lấy ảnh sản phẩm
     */
    public String getProductImage() {
        if (productVariant != null) {
            return productVariant.getImage();
        }
        if (product != null && product.getMediaList() != null && !product.getMediaList().isEmpty()) {
            return product.getMediaList().get(0).getMediaPath();
        }
        return null;
    }

    /**
     * Lấy stock hiện có
     */
    public Integer getAvailableStock() {
        if (productVariant != null) {
            return productVariant.getStock();
        }
        return product != null ? product.getStock() : 0;
    }
}
