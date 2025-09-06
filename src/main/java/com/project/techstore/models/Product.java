package com.project.techstore.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(name = "image")
    private String image;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "price_discount")
    private Long priceDiscount;

    @Column(name = "stock")
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "product_model_id")
    private ProductModel productModel;

    @OneToMany(mappedBy = "product")
    private List<ProductAttribute> productAttributes;
}
