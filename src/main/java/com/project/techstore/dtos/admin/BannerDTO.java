package com.project.techstore.dtos.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerDTO {
    private Long id;

    @NotBlank(message = "Banner title is required")
    private String title;

    private String description;

    private String image;

    private String link;

    private Integer order;

    @Default
    private Boolean active = true;
}
