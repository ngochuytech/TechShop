package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Review_attachments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "attachment_path", length = 255, nullable = false)
    private String attachmentPath;

    @Column(name = "attachment_type", length = 255, nullable = false)
    private String attachmentType;

    @Column(name = "comment", columnDefinition = "Text")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }
}
