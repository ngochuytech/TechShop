package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductFilterDTO {
    private String category;

    // Map: key = attribute_id, value = danh sách giá trị (e.g., {1: ["8GB", "16GB"], 2: ["Intel Core i5"]})
    private Map<String, List<String>> attributes;

    @JsonProperty("min_price")
    private Long minPrice;  // Giá tối thiểu (inclusive)

    @JsonProperty("max_price")
    private Long maxPrice;  // Giá tối đa (inclusive)

    private String brand;
}
