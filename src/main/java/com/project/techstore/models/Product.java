package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "price_discount")
    private Long priceDiscount;

    @Column(name = "stock")
    private Integer stock;

    @Column(columnDefinition = "JSON")
    private String spec;

    @ManyToOne
    @JoinColumn(name = "product_model_id")
    private ProductModel productModel;
}
