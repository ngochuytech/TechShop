package com.project.techstore.dtos.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {
    // Có thể là productId hoặc productVariantId
    // Nếu có productVariantId thì ưu tiên, không thì dùng productId
    private String productId;
    
    private String productVariantId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /**
     * Validate: phải có ít nhất 1 trong 2 (productId hoặc productVariantId)
     */
    public boolean isValid() {
        return (productId != null && !productId.isEmpty()) || 
               (productVariantId != null && !productVariantId.isEmpty());
    }
}
