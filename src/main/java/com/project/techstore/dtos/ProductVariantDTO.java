package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantDTO {
    
    @JsonProperty("product_id")
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @NotBlank(message = "Color is required")
    private String color;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;
    
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Long price; // Có thể null, sẽ sử dụng giá từ Product
    
}