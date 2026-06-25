package com.custard.ehr.pharmacy.application.mapper;

import com.custard.ehr.pharmacy.application.dto.AddSupplierDto;
import com.custard.ehr.pharmacy.application.dto.ViewSupplierDto;
import com.custard.ehr.pharmacy.domain.Supplier;

public class SupplierMapper {
    public static Supplier toEntity(AddSupplierDto dto) {

        Supplier supplier = new Supplier();
        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setCode(dto.getCode());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
        supplier.setAddress(dto.getAddress());
        supplier.setCity(dto.getCity());
        supplier.setState(dto.getState());
        supplier.setCountry(dto.getCountry());
        supplier.setPostalCode(dto.getPostalCode());
        supplier.setTaxId(dto.getTaxId());
        supplier.setPaymentTerms(dto.getPaymentTerms());
        supplier.setNotes(dto.getNotes());
        supplier.setRating(dto.getRating());
        supplier.setPreferred(dto.getPreferred());
        return supplier;
    }

    public static ViewSupplierDto toView(Supplier supplier) {
        return new ViewSupplierDto(
                supplier.getId(),
                supplier.getName(),
                supplier.getCode(),
                supplier.getContactPerson(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getAddress(),
                supplier.getCity(),
                supplier.getState(),
                supplier.getCountry(),
                supplier.getPostalCode(),
                supplier.getTaxId(),
                supplier.getPaymentTerms(),
                supplier.getNotes(),
                supplier.getRating(),
                supplier.getPreferred()
        );
    }
}
