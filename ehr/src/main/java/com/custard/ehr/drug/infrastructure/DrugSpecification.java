package com.custard.ehr.drug.infrastructure;

import com.custard.ehr.pharmacy.application.dto.ProductFilterRequest;
import com.custard.ehr.drug.domain.Drug;
import com.custard.ehr.pharmacy.domain.StockItem;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class DrugSpecification {

    public static Specification<Drug> search(String query) {
        return (root, criteriaQuery, cb) -> {
            if (query == null || query.isBlank()) {
                return cb.conjunction();
            }

            String pattern = "%" + query.trim().toLowerCase() + "%";

            // Using a list makes it easier to toggle fields or add them dynamically
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern),
                    cb.like(cb.lower(root.get("code")), pattern),
                    cb.like(cb.lower(root.get("form")), pattern)
            );
        };
    }

    public static Specification<Drug> activeOnly() {
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.isTrue(root.get("active"));
        };
    }

    public static Specification<Drug> priceRange(Double minPrice, Double maxPrice) {
        return ((root, query, cb) -> {
            if (maxPrice == null && maxPrice == null) {
                return cb.conjunction();
            }

            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("currentSellingPrice"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("currentSellingPrice"), minPrice);
            } else {
                return cb.lessThanOrEqualTo(root.get("currentSellingPrice"), maxPrice);
            }
        });
    }

    public static Specification<Drug> stockStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || "ALL".equals(status)) {
                return cb.conjunction();
            }

            // Subquery to calculate total stock
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<StockItem> stockItemRoot = subquery.from(StockItem.class);

            subquery.select(cb.sum(stockItemRoot.get("quantity")))
                    .where(cb.equal(stockItemRoot.get("product"), root));

            if ("IN_STOCK".equals(status)) {
                return cb.greaterThanOrEqualTo(subquery, 10L);
            } else if ("LOW_STOCK".equals(status)) {
                return cb.and(
                        cb.greaterThan(subquery, 0L),
                        cb.lessThan(subquery, 10L)
                );
            } else if ("OUT_OF_STOCK".equals(status)) {
                return cb.equal(subquery, 0L);
            }

            return cb.conjunction();
        };
    }

    // Combine all specifications
    public static Specification<Drug> buildSpecification(ProductFilterRequest filter) {
        return Specification
                .where(DrugSpecification.search(filter.getSearchTerm()))
                .and(DrugSpecification.priceRange(filter.getMinPrice(), filter.getMaxPrice()))
                .and(DrugSpecification.activeOnly()
                .and(DrugSpecification.stockStatus(filter.getStockStatus())));
    }


}
