package com.techknife.asset.service.impl;

import com.techknife.asset.dto.AssetWarrantyDTO;
import com.techknife.asset.entity.Asset;
import com.techknife.asset.entity.AssetWarranty;
import com.techknife.asset.repository.AssetRepository;
import com.techknife.asset.repository.AssetWarrantyRepository;
import com.techknife.asset.service.AssetWarrantyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetWarrantyServiceImpl implements AssetWarrantyService {

    private final AssetWarrantyRepository warrantyRepository;
    private final AssetRepository assetRepository;

    @Override
    public AssetWarrantyDTO createWarranty(AssetWarrantyDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with id: " + dto.getAssetId()));

        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("Warranty end date cannot be before warranty start date");
        }

        AssetWarranty warranty = AssetWarranty.builder()
                .assetId(asset.getId())
                .assetCode(asset.getAssetCode())
                .providerName(dto.getProviderName())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .coverageDetails(dto.getCoverageDetails())
                .terms(dto.getTerms())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        AssetWarranty saved = warrantyRepository.save(warranty);

        // Sync dates on asset
        asset.setWarrantyStartDate(dto.getStartDate());
        asset.setWarrantyEndDate(dto.getEndDate());
        assetRepository.save(asset);

        return mapToDTO(saved);
    }

    @Override
    public AssetWarrantyDTO updateWarranty(String id, AssetWarrantyDTO dto) {
        AssetWarranty warranty = warrantyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warranty record not found with id: " + id));

        if (dto.getStartDate() != null && dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("Warranty end date cannot be before warranty start date");
        }

        if (dto.getProviderName() != null) warranty.setProviderName(dto.getProviderName());
        if (dto.getContactPhone() != null) warranty.setContactPhone(dto.getContactPhone());
        if (dto.getContactEmail() != null) warranty.setContactEmail(dto.getContactEmail());
        if (dto.getStartDate() != null) warranty.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) warranty.setEndDate(dto.getEndDate());
        if (dto.getCoverageDetails() != null) warranty.setCoverageDetails(dto.getCoverageDetails());
        if (dto.getTerms() != null) warranty.setTerms(dto.getTerms());
        if (dto.getStatus() != null) warranty.setStatus(dto.getStatus());

        AssetWarranty saved = warrantyRepository.save(warranty);

        assetRepository.findById(saved.getAssetId()).ifPresent(asset -> {
            if (dto.getStartDate() != null) asset.setWarrantyStartDate(dto.getStartDate());
            if (dto.getEndDate() != null) asset.setWarrantyEndDate(dto.getEndDate());
            assetRepository.save(asset);
        });

        return mapToDTO(saved);
    }

    @Override
    public AssetWarrantyDTO getWarrantyByAsset(String assetId) {
        AssetWarranty warranty = warrantyRepository.findByAssetId(assetId)
                .orElseThrow(() -> new IllegalArgumentException("Warranty not found for asset id: " + assetId));
        return mapToDTO(warranty);
    }

    @Override
    public List<AssetWarrantyDTO> getAllWarranties() {
        return warrantyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AssetWarrantyDTO mapToDTO(AssetWarranty w) {
        return AssetWarrantyDTO.builder()
                .id(w.getId())
                .assetId(w.getAssetId())
                .assetCode(w.getAssetCode())
                .providerName(w.getProviderName())
                .contactPhone(w.getContactPhone())
                .contactEmail(w.getContactEmail())
                .startDate(w.getStartDate())
                .endDate(w.getEndDate())
                .coverageDetails(w.getCoverageDetails())
                .terms(w.getTerms())
                .status(w.getStatus())
                .createdAt(w.getCreatedAt())
                .updatedAt(w.getUpdatedAt())
                .build();
    }
}
