package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    @NotBlank(message = "The code is required")
    @Size(max = 50, message = "The maximum length of the code is 50 characters")
    private String code;

    @JsonProperty("discount_type")
    @NotBlank(message = "Discount type is required")
    @Size(max = 50, message = "The maximum length of the discount type is 50 characters")
    private String discountType;

    @JsonProperty("discount_value")
    @PositiveOrZero(message = "Discount value is required and positive")
    private Long discountValue;

    @JsonProperty("start_date")
    @NotNull(message = "Start date value is required")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    @NotNull(message = "End time value is required")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime endTime;

    @JsonProperty("min_order_value")
    @PositiveOrZero(message = "Min order value must be positive")
    private Long minOrderValue;

    @JsonProperty("max_discount")
    @PositiveOrZero(message = "Max discount must be positive")
    private Long maxDiscount;

    @JsonProperty("is_active")
    @NotNull(message = "Is active is required")
    private Boolean isActive;

    @JsonProperty("category_ids")
    private Set<Long> categories = new HashSet<>();

    @JsonProperty("brand_ids")
    private Set<Long> brands = new HashSet<>();
}
