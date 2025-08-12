package com.project.techstore.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    @JsonProperty("title")
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "The maximum length of title is 255 characters")
    private String title;

    @JsonProperty("content")
    @NotBlank(message = "Content is required")
    private String content;

    @JsonProperty("is_read")
    private Boolean isRead;

    @JsonProperty("user_id")
    private String userId;
}
