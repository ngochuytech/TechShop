package com.project.techstore.responses.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.techstore.models.Media;
import com.project.techstore.models.Product;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRespone {
    private String id;

    private String name;

    private List<String> images;

    private String status;

    private String description;

    private List<VariantResponse> colors;

    private Map<String, String> attributes;

    private String promotion;

    @JsonProperty("configuration_summary")
    private String configurationSummary;

    @JsonProperty("product_model_id")
    private Long productModelId;

    public static ProductRespone fromProduct(Product product){
        List<String> images = product.getMediaList().stream()
                .map(Media::getMediaPath)
                .toList();

        // Sử dụng ProductVariant để lấy các màu sắc
        List<VariantResponse> colorResponses = product.getVariants() != null ? 
                product.getVariants().stream()
                        .map(VariantResponse::fromProductVariant)
                        .toList() : 
                List.of();

        Map<String, String> attributes = new HashMap<>();
        product.getProductAttributes().forEach(pa -> {
            if (pa.getAttribute() != null) {
                attributes.put(pa.getAttribute().getDescription(), pa.getValue());
            }
        });

        return ProductRespone.builder()
                .id(product.getId())
                .name(product.getName())
                .images(images)
                .configurationSummary(product.getConfigurationSummary())
                .description(product.getDescription())
                .colors(colorResponses)
                .attributes(attributes)
                .promotion("") // Chưa xử lý
                .productModelId(product.getProductModel().getId())
                .build();
    }
}
