package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Banners")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Banner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image", length = 255, nullable = false)
    private String image;

    @Column(name = "link", length = 500)
    private String link;

    @Column(name = "banner_order")
    private Integer order;

    @Column(name = "active")
    private Boolean active = true;
}
