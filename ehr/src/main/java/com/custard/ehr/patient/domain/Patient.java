package com.custard.ehr.patient.domain;

import com.custard.ehr.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class Patient extends AuditableEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "patient_number", nullable = false, unique = true)
    )
    private PatientNumber patientNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate dateOfBirth;

    private String gender;

    private String phoneNumber;

    private String email;

    private String address;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Allergy> allergies = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChronicCondition> chronicConditions = new HashSet<>();

    protected Patient() {
    }

    public Patient(
            PatientNumber patientNumber,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String gender,
            String phoneNumber,
            String email,
            String address
    ) {
        this.patientNumber = patientNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    public void addAllergy(String name, String severity, String reaction) {
        this.allergies.add(new Allergy(this, name, severity, reaction));
    }

    public void addChronicCondition(String name, String notes) {
        this.chronicConditions.add(new ChronicCondition(this, name, notes));
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID getId() {
        return id;
    }

    public PatientNumber getPatientNumber() {
        return patientNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }

    public Set<Allergy> getAllergies() {
        return allergies;
    }

    public Set<ChronicCondition> getChronicConditions() {
        return chronicConditions;
    }
}