package com.project.techstore.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "province", length = 255, nullable = false)
    private String province;

    @Column(name = "ward", length = 255, nullable = false)
    private String ward;

    @Column(name = "home_address", length = 200, nullable = false)
    private String homeAddress;

    @Column(name = "suggested_name", length = 200)
    private String suggestedName;

    @Column(name = "phone", length = 10)
    private String phone;

    @ManyToOne  
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
