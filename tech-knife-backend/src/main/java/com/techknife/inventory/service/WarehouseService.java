package com.techknife.inventory.service;

import com.techknife.inventory.dto.WarehouseDTO;

import java.util.List;

public interface WarehouseService {
    List<WarehouseDTO> getAllWarehouses();
    WarehouseDTO getWarehouseById(String id);
    WarehouseDTO createWarehouse(WarehouseDTO dto);
    WarehouseDTO updateWarehouse(String id, WarehouseDTO dto);
    void deleteWarehouse(String id);
}
