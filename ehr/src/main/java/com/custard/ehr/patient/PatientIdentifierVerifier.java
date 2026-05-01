package com.custard.ehr.patient;

import com.custard.ehr.patient.application.ports.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface PatientIdentifierVerifier {
    boolean isValid(UUID patientId);
}

@Service
@Slf4j
class PatientLookupService implements PatientIdentifierVerifier {

    private static final Logger log = LoggerFactory.getLogger(PatientLookupService.class);
    private final PatientRepository patientRepository;

    PatientLookupService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public boolean isValid(UUID patientId) {
        log.info("Attempting to lookup patient ID: {}", patientId);
        return patientRepository.findById(patientId).isPresent();
    }

}
