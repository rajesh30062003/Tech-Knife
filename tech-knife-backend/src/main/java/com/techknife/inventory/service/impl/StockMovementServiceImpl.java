package com.techknife.inventory.service.impl;

import com.techknife.inventory.dto.StockMovementDTO;
import com.techknife.inventory.entity.InventoryItem;
import com.techknife.inventory.entity.InventoryStock;
import com.techknife.inventory.entity.StockMovement;
import com.techknife.inventory.repository.InventoryItemRepository;
import com.techknife.inventory.repository.InventoryStockRepository;
import com.techknife.inventory.repository.StockMovementRepository;
import com.techknife.inventory.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository movementRepository;
    private final InventoryItemRepository itemRepository;
    private final InventoryStockRepository stockRepository;

    @Override
    public StockMovementDTO recordMovement(StockMovementDTO dto) {
        InventoryItem item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + dto.getItemId()));

        String type = dto.getMovementType().toUpperCase();
        int qty = dto.getQuantity();

        if (qty <= 0) {
            throw new IllegalArgumentException("Movement quantity must be greater than zero");
        }

        // Apply movement logic to target or source warehouse stock
        if ("RECEIVE".equals(type) || "RETURN".equals(type)) {
            if (dto.getTargetWarehouseId() == null || dto.getTargetWarehouseId().isBlank()) {
                throw new IllegalArgumentException("Target warehouse ID is required for stock receipt");
            }
            updateStockQuantity(item, dto.getTargetWarehouseId(), qty);
        } else if ("ISSUE".equals(type) || "DAMAGE".equals(type)) {
            if (dto.getSourceWarehouseId() == null || dto.getSourceWarehouseId().isBlank()) {
                throw new IllegalArgumentException("Source warehouse ID is required for stock issue");
            }
            updateStockQuantity(item, dto.getSourceWarehouseId(), -qty);
        } else if ("TRANSFER".equals(type)) {
            if (dto.getSourceWarehouseId() == null || dto.getTargetWarehouseId() == null) {
                throw new IllegalArgumentException("Both source and target warehouses are required for transfer");
            }
            updateStockQuantity(item, dto.getSourceWarehouseId(), -qty);
            updateStockQuantity(item, dto.getTargetWarehouseId(), qty);
        }

        StockMovement movement = StockMovement.builder()
                .itemId(item.getId())
                .itemCode(item.getItemCode())
                .itemName(item.getName())
                .sourceWarehouseId(dto.getSourceWarehouseId())
                .sourceWarehouseName(dto.getSourceWarehouseName())
                .targetWarehouseId(dto.getTargetWarehouseId())
                .targetWarehouseName(dto.getTargetWarehouseName())
                .movementType(type)
                .quantity(qty)
                .referenceNumber(dto.getReferenceNumber())
                .performedBy(dto.getPerformedBy())
                .reason(dto.getReason())
                .movementDate(dto.getMovementDate() != null ? dto.getMovementDate() : LocalDate.now())
                .build();

        StockMovement saved = movementRepository.save(movement);
        return mapToDTO(saved);
    }

    private void updateStockQuantity(InventoryItem item, String warehouseId, int delta) {
        InventoryStock stock = stockRepository.findByItemIdAndWarehouseId(item.getId(), warehouseId)
                .orElseGet(() -> InventoryStock.builder()
                        .itemId(item.getId())
                        .itemCode(item.getItemCode())
                        .itemName(item.getName())
                        .warehouseId(warehouseId)
                        .quantity(0)
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build());

        int currentQty = stock.getQuantity() != null ? stock.getQuantity() : 0;
        int newQty = currentQty + delta;

        if (newQty < 0) {
            throw new IllegalArgumentException("Insufficient stock quantity in warehouse ID: " + warehouseId + ". Current: " + currentQty + ", Requested deduction: " + Math.abs(delta));
        }

        int reserved = stock.getReservedQuantity() != null ? stock.getReservedQuantity() : 0;
        stock.setQuantity(newQty);
        stock.setAvailableQuantity(newQty - reserved);
        stock.setLastUpdated(Instant.now());

        stockRepository.save(stock);
    }

    @Override
    public List<StockMovementDTO> getMovementsByItem(String itemId) {
        return movementRepository.findByItemId(itemId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StockMovementDTO> getAllMovements() {
        return movementRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private StockMovementDTO mapToDTO(StockMovement m) {
        return StockMovementDTO.builder()
                .id(m.getId())
                .itemId(m.getItemId())
                .itemCode(m.getItemCode())
                .itemName(m.getItemName())
                .sourceWarehouseId(m.getSourceWarehouseId())
                .sourceWarehouseName(m.getSourceWarehouseName())
                .targetWarehouseId(m.getTargetWarehouseId())
                .targetWarehouseName(m.getTargetWarehouseName())
                .movementType(m.getMovementType())
                .quantity(m.getQuantity())
                .referenceNumber(m.getReferenceNumber())
                .performedBy(m.getPerformedBy())
                .reason(m.getReason())
                .movementDate(m.getMovementDate())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
