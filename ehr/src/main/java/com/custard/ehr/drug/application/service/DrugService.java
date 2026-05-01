package com.custard.ehr.drug.application.service;

import com.custard.ehr.drug.application.dto.DrugResponse;
import com.custard.ehr.drug.application.ports.DrugRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class DrugService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DrugRepository drugRepository;

    public DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    public List<DrugResponse> search(String query) {
        log.debug("Searching drugs with query: {}", query);

        return drugRepository.findTop20ByNameContainingIgnoreCase(query)
                .stream()
                .map(DrugResponse::from)
                .toList();
    }

    public DrugResponse get(UUID id) {
        return drugRepository.findById(id)
                .map(DrugResponse::from)
                .orElseThrow(() -> new RuntimeException("Drug not found"));
    }
}