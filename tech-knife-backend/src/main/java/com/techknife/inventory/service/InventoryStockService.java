package com.techknife.inventory.service;

import com.techknife.inventory.dto.InventoryStockDTO;
import com.techknife.inventory.dto.StockAdjustmentRequest;

import java.util.List;

public interface InventoryStockService {
    List<InventoryStockDTO> getAllStocks();
    List<InventoryStockDTO> getStocksByItem(String itemId);
    List<InventoryStockDTO> getStocksByWarehouse(String warehouseId);
    InventoryStockDTO adjustStock(StockAdjustmentRequest request);
}
