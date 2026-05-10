package com.custard.ehr.drug.application.service;

import com.custard.ehr.drug.application.dto.DrugResponse;
import com.custard.ehr.drug.application.ports.DrugRepository;
import com.custard.ehr.drug.domain.Drug;
import com.custard.ehr.drug.infrastructure.DrugSpecification;
import com.custard.ehr.shared.domain.PageResultDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public PageResultDto<DrugResponse> search(String query, int size, int page, String sortBy) {
        log.debug("Searching drugs with query: {}", query);

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), Integer.MAX_VALUE);

        var pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.ASC, sortBy.isBlank() ? "name": sortBy)
        );

        var specification = DrugSpecification.activeOnly().and(DrugSpecification.search(query));

        Page<DrugResponse> search = drugRepository
                .findAll(specification, pageable)
                .map(DrugResponse::from);

        log.info("Search results {} ",  search.getTotalElements());

        return PageResultDto.from(search);
    }

    public DrugResponse get(UUID id) {
        return drugRepository.findById(id)
                .map(DrugResponse::from)
                .orElseThrow(() -> new RuntimeException("Drug not found"));
    }
}