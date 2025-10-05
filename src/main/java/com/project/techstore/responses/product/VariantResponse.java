package com.project.techstore.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.techstore.models.ProductVariant;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantResponse {
    private String id;
    
    private String name;

    private Long price;
    
    @JsonProperty("price_discount")
    private Long priceDiscount;
    
    private Integer stock;
    
    private String status;

    private String image;
    
    // Tạo phương thức để chuyển đổi từ ProductVariant sang ColorResponse
    public static VariantResponse fromProductVariant(ProductVariant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .name(variant.getColor())
                .price(variant.getPrice())
                .priceDiscount(variant.getPriceDiscount())
                .stock(variant.getStock())
                .status(variant.getStock() > 0 ? "Còn hàng" : "Tạm hết hàng")
                .image(variant.getImage() != null ? variant.getImage() : "")
                .build();
    }

}
