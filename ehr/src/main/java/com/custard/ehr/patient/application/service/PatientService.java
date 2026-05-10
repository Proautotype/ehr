package com.custard.ehr.patient.application.service;

import com.custard.ehr.patient.application.dto.AddAllergyRequest;
import com.custard.ehr.patient.application.dto.PatientResponse;
import com.custard.ehr.patient.application.dto.RegisterPatientRequest;
import com.custard.ehr.patient.application.dto.UpdatePatientRequest;
import com.custard.ehr.patient.application.mapper.PatientMapper;
import com.custard.ehr.patient.application.ports.PatientRepository;
import com.custard.ehr.patient.domain.Patient;
import com.custard.ehr.patient.domain.PatientNumber;
import com.custard.ehr.patient.infrastructure.PatientSpecifications;
import com.custard.ehr.shared.domain.PageResultDto;
import com.custard.ehr.shared.events.PatientRegisteredEvent;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j; // Added
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Slf4j // Added annotation
@Service
public class PatientService {

    private final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PatientMapper patientMapper;

    public PatientService(
            PatientRepository patientRepository,
            ApplicationEventPublisher eventPublisher,
            PatientMapper patientMapper
    ) {
        this.patientRepository = patientRepository;
        this.eventPublisher = eventPublisher;
        this.patientMapper = patientMapper;
    }

    @Transactional
    public PatientResponse register(RegisterPatientRequest request) {
        log.info("Attempting to register new patient: {} {}", request.firstName(), request.lastName());

        if (request.phoneNumber() != null &&
                patientRepository.existsByPhoneNumber(request.phoneNumber())) {
            log.warn("Registration failed: Patient with phone number {} already exists", request.phoneNumber());
            throw new BusinessException("Patient with this phone number already exists");
        }

        String generatedNumber = generatePatientNumber();
        Patient patient = new Patient(
                new PatientNumber(generatedNumber),
                request.firstName(),
                request.lastName(),
                request.dateOfBirth(),
                request.gender(),
                request.phoneNumber(),
                request.email(),
                request.address()
        );

        Patient saved = patientRepository.save(patient);
        log.info("Patient saved successfully with ID: {} and Patient Number: {}", saved.getId(), generatedNumber);

        eventPublisher.publishEvent(
                new PatientRegisteredEvent(
                        saved.getId(),
                        saved.getPatientNumber().getValue(),
                        SecurityUtils.getCurrentUserId().orElse(null),
                        Instant.now()
                )
        );
        log.debug("PatientRegisteredEvent published for patient ID: {}", saved.getId());

        return PatientResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PatientResponse getById(UUID id) {
        log.debug("Fetching patient by ID: {}", id);
        return patientRepository.findById(id)
                .map(PatientResponse::from)
                .orElseThrow(() -> {
                    log.warn("Patient lookup failed: No patient found with ID: {}", id);
                    return new NotFoundException("Patient not found");
                });
    }

    @Transactional(readOnly = true)
    public PatientResponse getByPatientNumber(String patientNumber) {
        log.debug("Fetching patient by Number: {}", patientNumber);
        return patientRepository.findByPatientNumberValue(patientNumber)
                .map(PatientResponse::from)
                .orElseThrow(() -> {
                    log.warn("Patient lookup failed: No patient found with Number: {}", patientNumber);
                    return new NotFoundException("Patient not found");
                });
    }


    public PageResultDto<PatientResponse> search(String query, int size, int page, String sortBy) {
        log.debug("Searching patients. query={}, page={}, size={}", query, page, size);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        var pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, sortBy.isBlank() ? "createdAt" : sortBy)
        );

        var specification = PatientSpecifications.activeOnly()
                .and(PatientSpecifications.search(query));

        var result = patientRepository.findAll(specification, pageable)
                .map(PatientResponse::from);

        log.info("Search results {} ", result.getContent().size());

        return PageResultDto.from(result);
    }

    @Transactional
    public PatientResponse addAllergy(UUID patientId, AddAllergyRequest request) {
        log.info("Adding allergy '{}' to patient ID: {}", request.name(), patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.warn("Add allergy failed: Patient ID {} not found", patientId);
                    return new NotFoundException("Patient not found");
                });

        patient.addAllergy(
                request.name(),
                request.severity(),
                request.reaction()
        );

        Patient saved = patientRepository.save(patient);
        log.info("Successfully added allergy to patient ID: {}", patientId);

        return PatientResponse.from(saved);
    }

    private String generatePatientNumber() {
        String num = "PAT-" + Year.now().getValue() + "-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
        log.trace("Generated patient number: {}", num);
        return num;
    }

    public PatientResponse update(UUID patientId, UpdatePatientRequest request) {
        try {
            log.info("Patient update request received \n{}", request);
            Patient patient = patientRepository.findById(patientId).orElseThrow();

            patientMapper.updatePatientFromRequest(request, patient);

            Patient updatedPatient = patientRepository.save(patient);
            return PatientResponse.from(updatedPatient);
        } catch (Exception e) {
            log.error("Error updating patient {}", patientId, e);
            throw new BusinessException(e.getMessage());
        }
    }

    private Patient findAndUpdateFields(UUID patientId, UpdatePatientRequest request) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();


        return patient;
    }

}