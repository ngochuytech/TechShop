package com.project.techstore.responses.product;

import com.project.techstore.models.ProductModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductModelResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String brand;

    public static ProductModelResponse fromProductModel(ProductModel productModel) {
        return ProductModelResponse.builder()
                .id(productModel.getId())
                .name(productModel.getName())
                .description(productModel.getDescription())
                .category(productModel.getCategory() != null ? productModel.getCategory().getName() : null)
                .brand(productModel.getBrand() != null ? productModel.getBrand().getName() : null)
                .build();
    }
}
