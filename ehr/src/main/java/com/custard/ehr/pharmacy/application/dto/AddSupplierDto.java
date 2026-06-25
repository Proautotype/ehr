package com.custard.ehr.pharmacy.application.dto;

import jakarta.validation.constraints.NotNull;

public class AddSupplierDto {

    @NotNull
    private String name;

    private String code;

    private String contactPerson;

    private String email;

    @NotNull
    private String phone;

    @NotNull
    private String address;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String taxId;

    private String paymentTerms;

    private String notes;

    private Integer rating = 3;

    private Boolean isPreferred = false;

    public AddSupplierDto(String name, String code, String contactPerson, String email, String phone, String address, String city, String state, String country, String postalCode, String taxId, String paymentTerms, String notes, Integer rating, Boolean isPreferred) {
        this.name = name;
        this.code = code;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.taxId = taxId;
        this.paymentTerms = paymentTerms;
        this.notes = notes;
        this.rating = rating;
        this.isPreferred = isPreferred;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getPreferred() {
        return isPreferred;
    }

    public void setPreferred(Boolean preferred) {
        isPreferred = preferred;
    }
}

