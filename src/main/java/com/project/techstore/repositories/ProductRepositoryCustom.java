package com.project.techstore.repositories;

import com.project.techstore.dtos.ProductFilterDTO;
import com.project.techstore.models.Product;
import com.project.techstore.models.ProductAttribute;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // Điều kiện attributes (RAM, CPU, GPU hoặc Size_screen, Resolution)
        if (filter.getAttributes() != null && !filter.getAttributes().isEmpty()) {
            for (Map.Entry<String, List<String>> entry : filter.getAttributes().entrySet()) {
                String attribute = entry.getKey();
                List<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    Join<Product, ProductAttribute> pa = product.join("productAttributes");
                    predicates.add(cb.equal(pa.get("attribute").get("name"), attribute));
                    // Tạo điều kiện OR cho từng giá trị với LIKE
                    List<Predicate> valuePredicates = new ArrayList<>();
                    for (String value : values) {
                        valuePredicates.add(cb.like(pa.get("value"), "%" + value + "%"));
                    }
                    predicates.add(cb.or(valuePredicates.toArray(new Predicate[0])));
                }
            }
        }

        query.select(product).distinct(true).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList();
    }
}
