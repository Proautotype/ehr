package com.custard.ehr.laboratory.application.service;

import com.custard.ehr.laboratory.application.dto.CreateLabTestRequest;
import com.custard.ehr.laboratory.application.dto.LabTestResponse;
import com.custard.ehr.laboratory.application.ports.LabTestRepository;
import com.custard.ehr.laboratory.domain.LabTest;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LabTestService {

    private final Logger log = LoggerFactory.getLogger(LabTestService.class);

    private final LabTestRepository labTestRepository;

    public LabTestService(LabTestRepository labTestRepository) {
        this.labTestRepository = labTestRepository;
    }

    @Transactional
    public LabTestResponse create(CreateLabTestRequest request) {
        log.info("Creating lab test {}", request.name());

        if (labTestRepository.existsByNameIgnoreCase(request.name())) {
            log.warn("Lab test creation blocked. Name already exists: {}", request.name());
            throw new BusinessException("Lab test already exists");
        }

        LabTest test = new LabTest(
                request.name(),
                request.code(),
                request.description(),
                request.price()
        );

        LabTest saved = labTestRepository.save(test);

        log.info("Lab test {} created successfully", saved.getId());

        return LabTestResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public LabTestResponse getById(UUID id) {
        return labTestRepository.findById(id)
                .map(LabTestResponse::from)
                .orElseThrow(() -> new NotFoundException("Lab test not found"));
    }

    @Transactional(readOnly = true)
    public List<LabTestResponse> search(String query) {
        log.debug("Searching lab tests with query {}", query);

        return labTestRepository.findTop20ByNameContainingIgnoreCaseAndActiveTrue(query)
                .stream()
                .map(LabTestResponse::from)
                .toList();
    }
}