package com.custard.ehr.pharmacy;

import com.custard.ehr.pharmacy.application.ports.DrugRepository;
import com.custard.ehr.pharmacy.domain.Drug;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public interface DrugIdentifierVerifier {

    Optional<DrugLookupView> findActiveDrug(UUID drugId);
}


@Service
@Slf4j
class DrugLookupService implements DrugIdentifierVerifier {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DrugRepository drugRepository;

    DrugLookupService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Override
    public Optional<DrugLookupView> findActiveDrug(UUID drugId) {
        log.debug("Looking up active drug: {}", drugId);

        return drugRepository.findById(drugId)
                .filter(Drug::isActive)
                .map(drug -> new DrugLookupView(
                        drug.getId(),
                        drug.getName(),
                        drug.getStrength(),
                        drug.getForm()
                ));
    }
}