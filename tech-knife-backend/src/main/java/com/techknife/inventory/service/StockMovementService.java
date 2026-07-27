package com.techknife.inventory.service;

import com.techknife.inventory.dto.StockMovementDTO;

import java.util.List;

public interface StockMovementService {
    StockMovementDTO recordMovement(StockMovementDTO dto);
    List<StockMovementDTO> getMovementsByItem(String itemId);
    List<StockMovementDTO> getAllMovements();
}
