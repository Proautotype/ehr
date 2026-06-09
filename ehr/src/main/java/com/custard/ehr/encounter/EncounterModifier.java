package com.custard.ehr.encounter;

import com.custard.ehr.encounter.application.dto.CreateEncounterRequest;
import com.custard.ehr.encounter.application.ports.EncounterRepository;
import com.custard.ehr.encounter.domain.Encounter;
import com.custard.ehr.identity.IdentityLookupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface EncounterModifier {
    UUID create(UUID patientId, String reasonForVisit, UUID orderedBy);
}

@Service
class EncounterModifierImpl implements EncounterModifier {

    private final EncounterRepository encounterRepository;

    EncounterModifierImpl(EncounterRepository encounterRepository, IdentityLookupService identityLookupService) {
        this.encounterRepository = encounterRepository;
    }

    @Override
    @Transactional
    public UUID create(UUID patientId, String reasonForVisit, UUID orderedBy) {
        CreateEncounterRequest request = new CreateEncounterRequest(
                patientId,
                reasonForVisit
        );


        Encounter encounter = new Encounter(
                request.patientId(),
                orderedBy,
                request.reasonForVisit()
        );
        Encounter save = encounterRepository.save(encounter);
        return save.getId();
    }

}
