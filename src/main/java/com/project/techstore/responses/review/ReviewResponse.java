package com.project.techstore.responses.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.techstore.models.Review;
import com.project.techstore.models.User;

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
public class ReviewResponse {
    private String id;
    private Integer rating;
    private String comment;
    private UserResponse user;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserResponse {
        private String id;
        private String name;
        private String avatar;

        public static UserResponse fromUser(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getFullName())
                    .avatar(user.getAvatar())
                    .build();
        }
    }

    public static ReviewResponse fromReview(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .user(UserResponse.fromUser(review.getUser()))
                .productId(review.getProduct().getId())
                .createdAt(review.getCreatedAt().toString())
                .updatedAt(review.getUpdatedAt().toString())
                .build();
    }
}
