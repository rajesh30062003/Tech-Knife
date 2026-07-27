package com.techknife.asset.service;

import com.techknife.asset.dto.AssetWarrantyDTO;

import java.util.List;

public interface AssetWarrantyService {
    AssetWarrantyDTO createWarranty(AssetWarrantyDTO dto);
    AssetWarrantyDTO updateWarranty(String id, AssetWarrantyDTO dto);
    AssetWarrantyDTO getWarrantyByAsset(String assetId);
    List<AssetWarrantyDTO> getAllWarranties();
}
