package com.custard.ehr.drug.infrastructure;

import com.custard.ehr.drug.domain.Drug;
import org.springframework.data.jpa.domain.Specification;

public class DrugSpecification {

    public static Specification<Drug> search(String query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (query == null || query.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String like = "%" + query.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), like),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), like),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("form")), like),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), like)
            );

        };
    }

    public static Specification<Drug> activeOnly(){
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.isTrue(root.get("active"));
        };
    }


}
