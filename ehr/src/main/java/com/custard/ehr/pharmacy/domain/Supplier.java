package com.custard.ehr.pharmacy.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class Supplier extends AuditableEntity {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", length = 50, unique = true)
    private String code;

    @Column(name = "contact_person", length = 255)
    private String contactPerson;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "rating")
    private Integer rating = 3;

    @Column(name = "is_preferred")
    private Boolean isPreferred = false;

    @OneToMany
    private List<Drug> drugs;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stock> stocks = new ArrayList<>();
}