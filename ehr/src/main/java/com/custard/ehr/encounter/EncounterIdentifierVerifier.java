package com.custard.ehr.encounter;

import com.custard.ehr.encounter.application.ports.EncounterRepository;
import com.custard.ehr.encounter.domain.EncounterStatus;
import com.custard.ehr.shared.domain.EncounterLookupView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public interface EncounterIdentifierVerifier {
    boolean isActive(UUID encounterId);
    Optional<EncounterLookupView> findActiveEncounter(UUID encounterId);
}


@Service
class EncounterLookupService implements EncounterIdentifierVerifier {

    private final Logger log  = LoggerFactory.getLogger(EncounterLookupService.class);
    private final EncounterRepository encounterRepository;

    EncounterLookupService(EncounterRepository encounterRepository) {
        this.encounterRepository = encounterRepository;
    }

    @Override
    public boolean isActive(UUID encounterId) {
        log.info("Validating active encounter ID: {}", encounterId);

        boolean exists = encounterRepository.findById(encounterId)
                .map(encounter -> encounter.getStatus() == EncounterStatus.ACTIVE)
                .orElse(false);

        log.debug("Encounter {} active validation result: {}", encounterId, exists);

        return exists;
    }

    @Override
    public Optional<EncounterLookupView> findActiveEncounter(UUID encounterId) {
        log.debug("Looking up active encounter: {}", encounterId);

        return encounterRepository.findById(encounterId)
                .filter(encounter -> encounter.getStatus() == EncounterStatus.ACTIVE)
                .map(encounter -> new EncounterLookupView(
                        encounter.getId(),
                        encounter.getPatientId()
                ));
    }
}