package com.custard.ehr.drug.infrastructure;

import com.custard.ehr.drug.domain.Drug;
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

    public static Specification<Drug> activeOnly(){
        return (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.isTrue(root.get("active"));
        };
    }


}
