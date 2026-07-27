package com.techknife.asset.service;

import com.techknife.asset.dto.AssetMovementDTO;

import java.util.List;

public interface AssetMovementService {
    AssetMovementDTO recordMovement(AssetMovementDTO dto);
    List<AssetMovementDTO> getMovementsByAsset(String assetId);
    List<AssetMovementDTO> getAllMovements();
}
