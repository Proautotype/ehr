package com.custard.ehr.drug.presentation;

import com.custard.ehr.drug.application.dto.DrugResponse;
import com.custard.ehr.drug.application.service.DrugService;
import com.custard.ehr.shared.infrastruture.web.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drugs")
@Slf4j
public class DrugController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping("/search")
    public ApiResponse<List<DrugResponse>> search(@RequestParam String query) {
        log.info("Drug search request: {}", query);
        return ApiResponse.success(drugService.search(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<DrugResponse> get(@PathVariable UUID id) {
        log.debug("Fetching drug ID: {}", id);
        return ApiResponse.success(drugService.get(id));
    }
}