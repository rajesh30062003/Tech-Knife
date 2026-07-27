package com.techknife.asset.service;

import com.techknife.asset.dto.AssetDTO;

import java.util.List;

public interface AssetService {
    List<AssetDTO> getAllAssets();
    AssetDTO getAssetById(String id);
    AssetDTO getAssetByCode(String assetCode);
    AssetDTO createAsset(AssetDTO dto);
    AssetDTO updateAsset(String id, AssetDTO dto);
    void deleteAsset(String id);
    List<AssetDTO> getAssetsByStatus(String status);
    List<AssetDTO> getAssetsByEmployee(String employeeId);
}
