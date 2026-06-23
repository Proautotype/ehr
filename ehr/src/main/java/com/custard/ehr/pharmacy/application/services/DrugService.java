package com.custard.ehr.pharmacy.application.services;

import com.custard.ehr.pharmacy.application.dto.DrugResponse;
import com.custard.ehr.pharmacy.application.dto.DrugStockItemDto;
import com.custard.ehr.pharmacy.application.dto.ProductFilterRequest;
import com.custard.ehr.pharmacy.application.ports.DrugRepository;
import com.custard.ehr.pharmacy.domain.Drug;
import com.custard.ehr.pharmacy.infrastructure.DrugSpecification;
import com.custard.ehr.shared.domain.PageResultDto;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Page<DrugStockItemDto> getProductStockItemsUsingTuple(ProductFilterRequest filterRequest) {
        Specification<Drug> spec = DrugSpecification.buildSpecification(filterRequest);
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), Sort.by(Sort.Direction.ASC, "name"));

        Page<Tuple> tuplePage = drugRepository.findProductsByStockWithSpecificationAsTuple(spec, filterRequest.getStockStatus(), pageable);

        List<DrugStockItemDto> content = tuplePage.getContent().stream().map(tuple -> {
            UUID id = tuple.get("id", UUID.class);
            String name = tuple.get("name", String.class);
            Long quantity = tuple.get("quantity", Long.class);
            String status = tuple.get("status", String.class);

            return new DrugStockItemDto(id, name, quantity, status);
        }).toList();

        return new PageImpl<>(content, pageable, tuplePage.getTotalElements());

    }

}

