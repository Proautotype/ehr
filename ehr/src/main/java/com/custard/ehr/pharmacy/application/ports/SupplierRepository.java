package com.custard.ehr.pharmacy.application.ports;


import com.custard.ehr.pharmacy.application.dto.AddSupplierDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface SupplierRepository {

    /**
     * Persists a new supplier entry.
     * * @param dto the supplier tracking requirements
     * @return the assigned UUID of the database record
     */
    UUID save(AddSupplierDto dto);

    /**
     * Looks up an individual supplier record matching the given ID.
     * * @param id primary unique tracking index
     * @return an Optional wrapper containing row projection metrics if found
     */
    Optional<Map<String, Object>> findById(UUID id);

    /**
     * Filters suppliers using flexible query options.
     * * @param query text match string targeting names, codes, or contact emails
     * @param isPreferred priority account tier filtering toggle
     * @param minRating filtering floor index for historic vendor scores
     * @return a collection of matching structural supplier profiles
     */
    List<Map<String, Object>> findByFilters(String query, Boolean isPreferred, Integer minRating);

    /**
     * Updates an existing supplier record.
     * * @param id target primary record index
     * @param dto operational specifications to write over existing keys
     */
    void update(UUID id, AddSupplierDto dto);

    /**
     * Deletes a supplier entry from the directory.
     * * @param id structural entry unique index
     * @return true if an active catalog row was wiped out, false otherwise
     */
    boolean deleteById(UUID id);
}