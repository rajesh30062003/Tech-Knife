package com.techknife.inventory.service.impl;

import com.techknife.inventory.dto.InventoryStockDTO;
import com.techknife.inventory.dto.StockAdjustmentRequest;
import com.techknife.inventory.entity.InventoryItem;
import com.techknife.inventory.entity.InventoryStock;
import com.techknife.inventory.entity.Warehouse;
import com.techknife.inventory.repository.InventoryItemRepository;
import com.techknife.inventory.repository.InventoryStockRepository;
import com.techknife.inventory.repository.WarehouseRepository;
import com.techknife.inventory.service.InventoryStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryStockServiceImpl implements InventoryStockService {

    private final InventoryStockRepository stockRepository;
    private final InventoryItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public List<InventoryStockDTO> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryStockDTO> getStocksByItem(String itemId) {
        return stockRepository.findByItemId(itemId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryStockDTO> getStocksByWarehouse(String warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryStockDTO adjustStock(StockAdjustmentRequest request) {
        if (request.getNewQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        InventoryItem item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + request.getItemId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with id: " + request.getWarehouseId()));

        InventoryStock stock = stockRepository.findByItemIdAndWarehouseId(request.getItemId(), request.getWarehouseId())
                .orElseGet(() -> InventoryStock.builder()
                        .itemId(item.getId())
                        .itemCode(item.getItemCode())
                        .itemName(item.getName())
                        .warehouseId(warehouse.getId())
                        .warehouseName(warehouse.getName())
                        .quantity(0)
                        .availableQuantity(0)
                        .reservedQuantity(0)
                        .build());

        int reserved = stock.getReservedQuantity() != null ? stock.getReservedQuantity() : 0;
        if (request.getNewQuantity() < reserved) {
            throw new IllegalArgumentException("New stock quantity cannot be lower than reserved stock quantity (" + reserved + ")");
        }

        stock.setQuantity(request.getNewQuantity());
        stock.setAvailableQuantity(request.getNewQuantity() - reserved);
        stock.setLastUpdated(Instant.now());

        InventoryStock saved = stockRepository.save(stock);
        return mapToDTO(saved);
    }

    private InventoryStockDTO mapToDTO(InventoryStock s) {
        return InventoryStockDTO.builder()
                .id(s.getId())
                .itemId(s.getItemId())
                .itemCode(s.getItemCode())
                .itemName(s.getItemName())
                .warehouseId(s.getWarehouseId())
                .warehouseName(s.getWarehouseName())
                .quantity(s.getQuantity())
                .availableQuantity(s.getAvailableQuantity())
                .reservedQuantity(s.getReservedQuantity())
                .lastUpdated(s.getLastUpdated())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
