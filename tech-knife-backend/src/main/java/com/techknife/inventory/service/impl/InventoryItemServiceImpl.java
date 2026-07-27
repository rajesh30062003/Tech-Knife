package com.techknife.inventory.service.impl;

import com.techknife.inventory.dto.InventoryItemDTO;
import com.techknife.inventory.entity.InventoryItem;
import com.techknife.inventory.repository.InventoryItemRepository;
import com.techknife.inventory.service.InventoryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository itemRepository;

    @Override
    public List<InventoryItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryItemDTO getItemById(String id) {
        InventoryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + id));
        return mapToDTO(item);
    }

    @Override
    public InventoryItemDTO getItemByCode(String itemCode) {
        InventoryItem item = itemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with code: " + itemCode));
        return mapToDTO(item);
    }

    @Override
    public InventoryItemDTO createItem(InventoryItemDTO dto) {
        if (itemRepository.existsByItemCode(dto.getItemCode())) {
            throw new IllegalArgumentException("Inventory item already exists with code: " + dto.getItemCode());
        }

        InventoryItem item = InventoryItem.builder()
                .itemCode(dto.getItemCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .unitOfMeasure(dto.getUnitOfMeasure() != null ? dto.getUnitOfMeasure() : "PCS")
                .reorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10)
                .minimumStock(dto.getMinimumStock() != null ? dto.getMinimumStock() : 5)
                .maximumStock(dto.getMaximumStock() != null ? dto.getMaximumStock() : 1000)
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .defaultSupplierId(dto.getDefaultSupplierId())
                .defaultSupplierName(dto.getDefaultSupplierName())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        InventoryItem saved = itemRepository.save(item);
        return mapToDTO(saved);
    }

    @Override
    public InventoryItemDTO updateItem(String id, InventoryItemDTO dto) {
        InventoryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + id));

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getCategoryId() != null) item.setCategoryId(dto.getCategoryId());
        if (dto.getCategoryName() != null) item.setCategoryName(dto.getCategoryName());
        if (dto.getUnitOfMeasure() != null) item.setUnitOfMeasure(dto.getUnitOfMeasure());
        if (dto.getReorderLevel() != null) item.setReorderLevel(dto.getReorderLevel());
        if (dto.getMinimumStock() != null) item.setMinimumStock(dto.getMinimumStock());
        if (dto.getMaximumStock() != null) item.setMaximumStock(dto.getMaximumStock());
        if (dto.getPurchasePrice() != null) item.setPurchasePrice(dto.getPurchasePrice());
        if (dto.getSellingPrice() != null) item.setSellingPrice(dto.getSellingPrice());
        if (dto.getDefaultSupplierId() != null) item.setDefaultSupplierId(dto.getDefaultSupplierId());
        if (dto.getDefaultSupplierName() != null) item.setDefaultSupplierName(dto.getDefaultSupplierName());
        if (dto.getStatus() != null) item.setStatus(dto.getStatus());

        InventoryItem saved = itemRepository.save(item);
        return mapToDTO(saved);
    }

    @Override
    public void deleteItem(String id) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Inventory item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    private InventoryItemDTO mapToDTO(InventoryItem i) {
        return InventoryItemDTO.builder()
                .id(i.getId())
                .itemCode(i.getItemCode())
                .name(i.getName())
                .description(i.getDescription())
                .categoryId(i.getCategoryId())
                .categoryName(i.getCategoryName())
                .unitOfMeasure(i.getUnitOfMeasure())
                .reorderLevel(i.getReorderLevel())
                .minimumStock(i.getMinimumStock())
                .maximumStock(i.getMaximumStock())
                .purchasePrice(i.getPurchasePrice())
                .sellingPrice(i.getSellingPrice())
                .defaultSupplierId(i.getDefaultSupplierId())
                .defaultSupplierName(i.getDefaultSupplierName())
                .status(i.getStatus())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .createdBy(i.getCreatedBy())
                .updatedBy(i.getUpdatedBy())
                .build();
    }
}
