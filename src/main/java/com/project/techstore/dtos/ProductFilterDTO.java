package com.project.techstore.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductFilterDTO {
    private String category;

    // Map: key = attribute_id, value = danh sách giá trị (e.g., {1: ["8GB", "16GB"], 2: ["Intel Core i5"]})
    private Map<String, List<String>> attributes;
}
