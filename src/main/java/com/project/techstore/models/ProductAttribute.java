package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attributes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    private String value;
}
