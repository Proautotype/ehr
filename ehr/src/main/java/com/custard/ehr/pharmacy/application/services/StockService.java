package com.custard.ehr.pharmacy.application.services;

import com.custard.ehr.drug.DrugIdentifierVerifier;
import com.custard.ehr.drug.DrugLookupView;
import com.custard.ehr.pharmacy.application.dto.AddStockRequest;
import com.custard.ehr.pharmacy.application.dto.CreateStockItemRequest;
import com.custard.ehr.pharmacy.application.dto.StockItemResponse;
import com.custard.ehr.pharmacy.application.ports.StockMovementRepository;
import com.custard.ehr.pharmacy.application.ports.StockRepository;
import com.custard.ehr.pharmacy.domain.StockItem;
import com.custard.ehr.pharmacy.domain.StockMovement;
import com.custard.ehr.pharmacy.domain.StockMovementType;
import com.custard.ehr.pharmacy.presentation.PharmacyController;
import com.custard.ehr.shared.events.StockAddedEvent;
import com.custard.ehr.shared.exception.BusinessException;
import com.custard.ehr.shared.exception.NotFoundException;
import com.custard.ehr.shared.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StockService {

    private final Logger log = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;
    private final DrugIdentifierVerifier drugIdentifierVerifier;
    private final ApplicationEventPublisher eventPublisher;

    public StockService(
            StockRepository stockRepository,
            StockMovementRepository stockMovementRepository,
            DrugIdentifierVerifier drugIdentifierVerifier,
            ApplicationEventPublisher eventPublisher
    ) {
        this.stockRepository = stockRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.drugIdentifierVerifier = drugIdentifierVerifier;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public StockItemResponse create(CreateStockItemRequest request) {
        log.info("Creating stock item for drug {}", request.drugId());

        if (stockRepository.findByDrugId(request.drugId()).isPresent()) {
            log.warn("Stock item creation blocked. Drug {} already has stock record", request.drugId());
            throw new BusinessException("Stock item already exists for this drug");
        }

        DrugLookupView drug = drugIdentifierVerifier.findActiveDrug(request.drugId())
                .orElseThrow(() -> {
                    log.warn("Stock item creation blocked. Drug {} not found or inactive", request.drugId());
                    return new BusinessException("Drug does not exist or is inactive");
                });

        UUID performedBy = SecurityUtils.requireCurrentUserId();

        StockItem stockItem = new StockItem(
                drug.drugId(),
                drug.name(),
                drug.strength(),
                drug.form(),
                request.openingQuantity()
        );

        StockItem saved = stockRepository.save(stockItem);

        if (request.openingQuantity() > 0) {
            stockMovementRepository.save(
                    new StockMovement(
                            saved.getDrugId(),
                            saved.getDrugName(),
                            StockMovementType.STOCK_IN,
                            request.openingQuantity(),
                            saved.getId(),
                            performedBy,
                            "Opening stock quantity"
                    )
            );

            publishStockAddedEvent(saved, request.openingQuantity(), performedBy);
        }

        log.info(
                "Stock item {} created for drug {} with opening quantity {}",
                saved.getId(),
                saved.getDrugName(),
                saved.getQuantityAvailable()
        );

        return StockItemResponse.from(saved);
    }

    @Transactional
    public StockItemResponse addStock(UUID drugId, AddStockRequest request) {
        log.info("Adding {} units to stock for drug {}", request.quantity(), drugId);

        StockItem stockItem = stockRepository.findByDrugId(drugId)
                .orElseThrow(() -> {
                    log.warn("Stock addition failed. Stock item for drug {} not found", drugId);
                    return new NotFoundException("Stock item not found");
                });

        UUID performedBy = SecurityUtils.requireCurrentUserId();

        stockItem.add(request.quantity());
        StockItem saved = stockRepository.save(stockItem);

        stockMovementRepository.save(
                new StockMovement(
                        saved.getDrugId(),
                        saved.getDrugName(),
                        StockMovementType.STOCK_IN,
                        request.quantity(),
                        saved.getId(),
                        performedBy,
                        request.note() == null ? "Stock added" : request.note()
                )
        );

        publishStockAddedEvent(saved, request.quantity(), performedBy);

        log.info(
                "Stock updated for drug {}. Added={}, Available={}",
                saved.getDrugName(),
                request.quantity(),
                saved.getQuantityAvailable()
        );

        return StockItemResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public StockItemResponse getByDrugId(UUID drugId) {
        log.debug("Fetching stock item for drug {}", drugId);

        return stockRepository.findByDrugId(drugId)
                .map(StockItemResponse::from)
                .orElseThrow(() -> {
                    log.warn("Stock lookup failed. Drug {} has no stock record", drugId);
                    return new NotFoundException("Stock item not found");
                });
    }

    @Transactional(readOnly = true)
    public List<StockItemResponse> search(String query) {
        log.debug("Searching stock with query {}", query);

        return stockRepository.findTop20ByDrugNameContainingIgnoreCase(query)
                .stream()
                .map(StockItemResponse::from)
                .toList();
    }

    private void publishStockAddedEvent(
            StockItem stockItem,
            Integer quantity,
            UUID performedBy
    ) {
        eventPublisher.publishEvent(
                new StockAddedEvent(
                        stockItem.getId(),
                        stockItem.getDrugId(),
                        stockItem.getDrugName(),
                        quantity,
                        performedBy,
                        Instant.now()
                )
        );

        log.debug(
                "StockAddedEvent published. StockItem={}, Drug={}, Quantity={}",
                stockItem.getId(),
                stockItem.getDrugId(),
                quantity
        );
    }
}