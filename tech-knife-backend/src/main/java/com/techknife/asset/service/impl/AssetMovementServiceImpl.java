package com.techknife.asset.service.impl;

import com.techknife.asset.dto.AssetMovementDTO;
import com.techknife.asset.entity.Asset;
import com.techknife.asset.entity.AssetMovement;
import com.techknife.asset.repository.AssetMovementRepository;
import com.techknife.asset.repository.AssetRepository;
import com.techknife.asset.service.AssetMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetMovementServiceImpl implements AssetMovementService {

    private final AssetMovementRepository movementRepository;
    private final AssetRepository assetRepository;

    @Override
    public AssetMovementDTO recordMovement(AssetMovementDTO dto) {
        Asset asset = assetRepository.findById(dto.getAssetId())
                .orElseThrow(() -> new IllegalArgumentException("Asset not found with id: " + dto.getAssetId()));

        AssetMovement movement = AssetMovement.builder()
                .assetId(asset.getId())
                .assetCode(asset.getAssetCode())
                .assetName(asset.getName())
                .fromLocation(dto.getFromLocation() != null ? dto.getFromLocation() : asset.getCurrentLocation())
                .toLocation(dto.getToLocation())
                .fromBranch(dto.getFromBranch() != null ? dto.getFromBranch() : asset.getAssignedBranchId())
                .toBranch(dto.getToBranch())
                .movementDate(dto.getMovementDate() != null ? dto.getMovementDate() : LocalDate.now())
                .movedBy(dto.getMovedBy())
                .reason(dto.getReason())
                .status(dto.getStatus() != null ? dto.getStatus() : "COMPLETED")
                .build();

        AssetMovement saved = movementRepository.save(movement);

        // Update asset location
        if (dto.getToLocation() != null) {
            asset.setCurrentLocation(dto.getToLocation());
        }
        if (dto.getToBranch() != null) {
            asset.setAssignedBranchId(dto.getToBranch());
        }
        assetRepository.save(asset);

        return mapToDTO(saved);
    }

    @Override
    public List<AssetMovementDTO> getMovementsByAsset(String assetId) {
        return movementRepository.findByAssetId(assetId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetMovementDTO> getAllMovements() {
        return movementRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AssetMovementDTO mapToDTO(AssetMovement m) {
        return AssetMovementDTO.builder()
                .id(m.getId())
                .assetId(m.getAssetId())
                .assetCode(m.getAssetCode())
                .assetName(m.getAssetName())
                .fromLocation(m.getFromLocation())
                .toLocation(m.getToLocation())
                .fromBranch(m.getFromBranch())
                .toBranch(m.getToBranch())
                .movementDate(m.getMovementDate())
                .movedBy(m.getMovedBy())
                .reason(m.getReason())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
