package com.project.techstore.dtos.admin.promotion;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    private String title;

    @JsonProperty("code")
    @NotBlank(message = "The code is required")
    @Size(max = 50, message = "The maximum length of the code is 50 characters")
    private String code;

    private String description;

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
    @Nullable
    @PositiveOrZero(message = "Max discount must be positive")
    private Long maxDiscount;

    @JsonProperty("usage_limit_per_user")
    @Nullable
    @PositiveOrZero(message = "Usage limit per user must be positive")
    private Integer usageLimitPerUser;

    @JsonProperty("is_for_new_customer")
    @Nullable
    private Boolean isForNewCustomer;

    @JsonProperty("total_usage_limit")
    @Nullable
    @PositiveOrZero(message = "Total usage limit must be positive")
    private Integer totalUsageLimit;
}
