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
public class ReviewAttachmentDTO {
    @JsonProperty("attachment_path")
    @Size(max = 255, message = "The maximum length of attachment path is 255 characters")
    @NotBlank(message = "Attachment path is required")
    private String attachmentPath;

    @JsonProperty("attachment_type")
    @Size(max = 255, message = "The maximum length of attachment type is 255 characters")
    @NotBlank(message = "Attachment path is required")
    private String attachmentType;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("review_id")
    @NotBlank(message = "ReviewId is required")
    private String reviewId;
}
