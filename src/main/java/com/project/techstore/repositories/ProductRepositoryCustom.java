package com.project.techstore.repositories;

import com.project.techstore.dtos.product.ProductFilterDTO;
import com.project.techstore.models.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Product> findProductsByFilters(ProductFilterDTO filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();

        // Điều kiện category
        predicates.add(cb.equal(product.get("productModel").get("category").get("name"), filter.getCategory()));

        // Điều kiện brand
        if(filter.getBrand() != null && !filter.getBrand().isEmpty()){
            predicates.add(cb.equal(product.get("productModel").get("brand").get("name"), filter.getBrand()));
        }

        // Điều kiện khoảng giá (lọc theo price)
        if (filter.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get("price"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get("price"), filter.getMaxPrice()));
        }

        // Thêm điều kiện không bị xóa
        predicates.add(cb.equal(product.get("isDeleted"), false));

        query.select(product).distinct(true).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }
}
