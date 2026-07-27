package com.techknife.inventory.service;

import com.techknife.inventory.dto.InventoryItemDTO;

import java.util.List;

public interface InventoryItemService {
    List<InventoryItemDTO> getAllItems();
    InventoryItemDTO getItemById(String id);
    InventoryItemDTO getItemByCode(String itemCode);
    InventoryItemDTO createItem(InventoryItemDTO dto);
    InventoryItemDTO updateItem(String id, InventoryItemDTO dto);
    void deleteItem(String id);
}
