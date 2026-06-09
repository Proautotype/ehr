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
        this.active = true;
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

    public void setChronicConditions(Set<ChronicCondition> chronicConditions) {
        this.chronicConditions = chronicConditions;
    }

    public void setAllergies(Set<Allergy> allergies) {
        this.allergies = allergies;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPatientNumber(PatientNumber patientNumber) {
        this.patientNumber = patientNumber;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", patientNumber=" + patientNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", active=" + active +
                ", allergies=" + allergies +
                ", chronicConditions=" + chronicConditions +
                '}';
    }
}