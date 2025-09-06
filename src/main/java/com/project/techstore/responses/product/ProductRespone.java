package com.project.techstore.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.techstore.models.Product;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRespone {
    private String id;

    private String name;

    private String image;

    private Long price;

    @JsonProperty("price_discount")
    private Long priceDiscount;

    private String spec;

    public static ProductRespone fromProduct(Product product){
        return ProductRespone.builder()
                .id(product.getId())
                .name(product.getName())
                .image(product.getImage())
                .price(product.getPrice())
                .priceDiscount(product.getPriceDiscount())
                .build();
    }
}
