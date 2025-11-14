package com.project.techstore.responses.favorite;

import com.project.techstore.models.Favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteResponse {
    private String id;
    private String userId;
    private ProductResponse product;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    static class ProductResponse {
        private String id;
        private String name;
        private String category;
        private String image;
        private Long price;
    }

    public static FavoriteResponse fromFavourite(Favorite favourite) {
        ProductResponse product = ProductResponse.builder()
                .id(favourite.getProduct().getId())
                .name(favourite.getProduct().getName())
                .category(favourite.getProduct().getProductModel().getCategory().getName())
                .image(favourite.getProduct().getMediaList() != null && !favourite.getProduct().getMediaList().isEmpty()
                        ? favourite.getProduct().getMediaList().stream()
                                .filter(media -> media.getIsPrimary() == true)
                                .findFirst()
                                .map(media -> media.getMediaPath())
                                .orElse("")
                        : "")
                .price(favourite.getProduct().getPrice())
                .build();

        return FavoriteResponse.builder()
                .id(favourite.getId())
                .userId(favourite.getUser().getId())
                .product(product)
                .build();
    }

}
