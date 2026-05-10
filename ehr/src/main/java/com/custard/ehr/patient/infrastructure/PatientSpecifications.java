package com.custard.ehr.patient.infrastructure;

import com.custard.ehr.patient.domain.Patient;
import org.springframework.data.jpa.domain.Specification;

public class PatientSpecifications {

    private PatientSpecifications() {}

    public static Specification<Patient> search(String query) {
        return (root, criteriaQuery, criteriaBuilder) ->{
            if(query == null || query.isEmpty()){
                return criteriaBuilder.conjunction();
            }

            String like = "%" + query.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phoneNumber")), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("patientNumber")), like)
            );
        };
    }

    public static Specification<Patient> activeOnly() {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("active"));
    }

}
